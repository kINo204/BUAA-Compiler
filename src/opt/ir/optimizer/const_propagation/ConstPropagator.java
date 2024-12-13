package opt.ir.optimizer.const_propagation;

import ir.datastruct.Instr;
import ir.datastruct.operand.Const;
import ir.datastruct.operand.Var;
import opt.ir.datastruct.BBlock;
import opt.ir.datastruct.Cfg;
import opt.ir.datastruct.Unit;

import java.util.*;

public class ConstPropagator {
    private final Cfg cfg;

    public ConstPropagator(Cfg cfg) { this.cfg = cfg; }

    private static final class ConstValue {
        private final Unit unit;
        private final Const value;
        private ConstValue(Unit unit, Const value) {
            this.unit = unit;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ConstValue that)) return false;

            if (!unit.equals(that.unit)) return false;
            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            int result = unit.hashCode();
            result = 31 * result + value.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return String.format("%s := %s", unit, value);
        }
    }

    private boolean isConstAssign(Instr instr) {
        switch (instr.op) {
            case MOVE, STARR, STREF -> {
                return instr.main instanceof Const;
            }
            default -> { return false; }
        }
    }

    private boolean isAssign(Instr instr) {
        return switch (instr.op) {
            case LDARR, STARR, ADDR, LDREF, STREF,
                    ADD, SUB, MUL, DIV, MOD, OR, AND, MOVE,
                    LSS, LEQ, GRE, GEQ, EQL, NEQ -> true;
            case CALL -> instr.res != null;
            default -> false;
        };
    }

    private Unit assignedUnit(Instr instr) {
        if (!isAssign(instr)) return null;
        assert instr.res != null;
        switch (instr.op) {
            case STARR, STREF   -> {
                return new Unit(instr.res, instr.supl);
            }
            default -> { return new Unit(instr.res); }
        }
    }

    private ConstValue constValueOf(Instr instr) {
        if (!isConstAssign(instr)) {
            return null;
        } else {
            return new ConstValue(
                    assignedUnit(instr),
                    (Const) instr.main);
        }
    }

    public boolean run() {
        boolean mod = false, modPass;

        do {
            modPass = pass();
            mod |= modPass;
        } while (modPass);

        return mod;
    }

    private boolean pass() {
        boolean mod = false;

        // Perform local propagation.
        for (BBlock block : cfg.blocks) { mod |= propagateBlock(block); }

        // Calculate global dataflow info.
        final ArrayList<Instr> instrs = new ArrayList<>();
        cfg.blocks.forEach(b -> instrs.addAll(b.instrs));

        final HashMap<Unit, HashSet<ConstValue>> constValues = new HashMap<>();
        for (Instr i : instrs) {
            if (isConstAssign(i)) {
                final Unit unit = assignedUnit(i);
                assert unit != null;
                if (!unit.uncertain()) {
                    final ConstValue constValue = constValueOf(i);
                    if (!constValues.containsKey(unit)) { constValues.put(unit, new HashSet<>()); }
                    constValues.get(unit).add(constValue);
                }
            } else if (isAssign(i)) {
                final Unit unit = assignedUnit(i);
                assert unit != null;
                if (!unit.uncertain()) {
                    if (!constValues.containsKey(unit)) { constValues.put(unit, new HashSet<>()); }
                }
            }
        }

        // Calculate Gen/Kill for each instruction.
        final HashMap<Instr, HashSet<ConstValue>> instrGen = new HashMap<>();
        for (Instr i : instrs) {
            if (isConstAssign(i)) {
                ConstValue constValue = constValueOf(i);
                assert constValue != null;
                if (constValue.unit.uncertain()) {
                    instrGen.put(i, new HashSet<>());
                } else {
                    instrGen.put(i, new HashSet<>(List.of(constValue)));
                }
            } else {
                instrGen.put(i, new HashSet<>());
            }
        }

        final HashMap<Instr, HashSet<ConstValue>> instrKill = new HashMap<>();
        for (Instr i : instrs) {
            if (isConstAssign(i)) {
                final Unit u = assignedUnit(i);
                assert u != null;
                if (u.uncertain()) {
                    instrKill.put(i, new HashSet<>());
                } else {
                    final HashSet<ConstValue> vals = new HashSet<>(constValues.get(u));
                    vals.removeAll(instrGen.get(i));
                    instrKill.put(i, vals);
                }
            } else if (isAssign(i)) {
                final Unit u = assignedUnit(i);
                assert u != null;
                if (u.uncertain()) {
                    instrKill.put(i, new HashSet<>());
                } else {
                    instrKill.put(i, new HashSet<>(constValues.get(u)));
                }
            } else {
                instrKill.put(i, new HashSet<>());
            }
        }

        /* Supplementary instr kill:
        * 1. func call --> all global variable's const values;
        * 2. array param --> all const values of the unit with the array */
        final HashSet<ConstValue> globalConstValues = new HashSet<>();
        for (Unit unit : constValues.keySet()) {
            if (unit.operand instanceof Var var && var.isGlobal) {
                globalConstValues.addAll(constValues.get(unit));
            }
        }
        for (Instr instr : instrs) {
            if (instr.op == Instr.Operator.CALL) {
                instrKill.get(instr).addAll(globalConstValues);
            }
            if (instr.op == Instr.Operator.ADDR
                    && instr.main instanceof Var v && (v.isArray || v.isReference)) {
                final HashSet<ConstValue> toKill = new HashSet<>();
                for (Unit unit : constValues.keySet()) {
                    if (unit.operand.equals(v)) {
                        toKill.addAll(constValues.get(unit));
                    }
                }
                instrKill.get(instr).addAll(toKill);
            }
        }

        // Calculate Gen/Kill for each block.
        final HashMap<BBlock, HashSet<ConstValue>> blockGen = new HashMap<>();
        final HashMap<BBlock, HashSet<ConstValue>> blockKill = new HashMap<>();

        for (BBlock block : cfg.blocks) {
            HashSet<ConstValue> gen = new HashSet<>();
            for (int i = 0; i < block.instrs.size(); i++) {
                Instr instr = block.instrs.get(i);
                final HashSet<ConstValue> item = new HashSet<>(instrGen.get(instr));
                for (int j = i + 1; j < block.instrs.size(); j++) {
                    item.removeAll(instrKill.get(block.instrs.get(j)));
                }
                gen.addAll(item);
            }
            blockGen.put(block, gen);
        }

        for (BBlock block : cfg.blocks) {
            HashSet<ConstValue> kill = new HashSet<>();
            for (Instr instr : block.instrs) {
                kill.addAll(instrKill.get(instr));
            }
            blockKill.put(block, kill);
        }

        // Calculate dataflow info for blocks.
        final HashMap<BBlock, HashSet<ConstValue>> In = new HashMap<>();
        final HashMap<BBlock, HashSet<ConstValue>> Out = new HashMap<>();

        for (BBlock block : cfg.blocks) {
            In.put(block, new HashSet<>());
            Out.put(block, new HashSet<>());
        }

        boolean m;
        do {
            m = false;
            for (BBlock block : cfg.blocks) {
                HashSet<ConstValue> set = new HashSet<>(In.get(block));
                set.removeAll(blockKill.get(block));
                set.addAll(blockGen.get(block));

                m |= !set.equals(Out.get(block));

                Out.replace(block, set);

                // Update In.
                for (BBlock block1 : cfg.blocks) {
                    HashSet<ConstValue> in = new HashSet<>();
                    boolean isFirst = true;
                    for (BBlock block2 : block1.prevSet) {
                        if (cfg.entry == block2) continue;
                        if (isFirst) {
                            isFirst = false;
                            in.addAll(Out.get(block2));
                        } else {
                            in.retainAll(Out.get(block2));
                        }
                    }
                    In.replace(block1, in);
                }
            }
        } while (m);

        // Perform global propagation.
        for (BBlock block : cfg.blocks) {
            final HashMap<Unit, Const> constMap = new HashMap<>();
            for (ConstValue constValue : In.get(block)) {
                assert !constMap.containsKey(constValue.unit);
                constMap.put(constValue.unit, constValue.value);
            }
            propagateBlock(block, constMap);
        }

        return mod;
    }

    // Used for local propagation.
    private boolean propagateBlock(BBlock block) {
        return propagateBlock(block, new HashMap<>());
    }

    // Used for global propagation.
    private boolean propagateBlock(BBlock block, HashMap<Unit, Const> set) {
        boolean mod = false;

        for (Instr instr : block.instrs) {
            // We first execute propagation, so the `set` update later would be correct.
            /* First, we handle each field of the IR instruction as a unit, and try finding
            substitution consts for them.
            Doing this ahead of the next step also create opportunity for next, e.g., changing
             t = a[b] to t = a[2], in which the previous unit "a[b]" may not be tracked. */
            final Unit uMain = instr.main == null ? null : new Unit(instr.main);
            final Unit uSupl = instr.supl == null ? null : new Unit(instr.supl);
            Const cMain = set.get(uMain), cSupl = set.get(uSupl);
            if (cMain != null) {
                mod = true;
                instr.main = cMain;
            }
            if (cSupl != null) {
                mod = true;
                instr.supl = cSupl;
            }

            /* Then, for instructions with associated-typed operands like LOAD(ARR) and DEREF, we need
             * to look at these units as well */
            if (instr.op == Instr.Operator.LDARR || instr.op == Instr.Operator.LDREF) {
                final Unit u = new Unit(instr.main, instr.supl); // This is the associated type being accessed.
                Const c = set.get(u);
                if (c != null) { // After we change the operand, the instruction's operator type changed!
                    mod = true;
                    instr.op = Instr.Operator.MOVE;
                    instr.main = c;
                    instr.supl = null;
                }
            }

            /* After modifying the next instruction, we used the modified new instruction to calculate
             * the dataflow info, by updating the `set`. */
            if (isConstAssign(instr)) {
                ConstValue constValue = constValueOf(instr);
                assert constValue != null;
                if (!constValue.unit.uncertain())
                    set.put(constValue.unit, constValue.value);
            } else if (isAssign(instr)) {
                Unit u = assignedUnit(instr);
                assert u != null;
                set.remove(u);
            }
            if (instr.op == Instr.Operator.CALL) {
                set.keySet().removeIf(u -> (u.operand instanceof Var v && v.isGlobal));
            }
            if (instr.op == Instr.Operator.ADDR
                    && instr.main instanceof Var v && (v.isArray || v.isReference)) {
                set.keySet().removeIf(u -> u.operand.equals(v));
            }
        }

        return mod;
    }
}
