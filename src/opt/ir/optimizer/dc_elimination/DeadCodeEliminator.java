package opt.ir.optimizer.dc_elimination;

import ir.datastruct.Instr;
import ir.datastruct.operand.Const;
import ir.datastruct.operand.Operand;
import ir.datastruct.operand.Reg;
import ir.datastruct.operand.Var;
import opt.ir.datastruct.BBlock;
import opt.ir.datastruct.Cfg;
import opt.ir.datastruct.Unit;

import java.util.*;

public class DeadCodeEliminator {
    private final Cfg cfg;

    public DeadCodeEliminator(Cfg cfg) {
        this.cfg = cfg;
    }

    /* We adopt the method of "finding necessary instructions" to determine
    * dead-or-not instructions. First, we have to determine what kind of
    * instructions are "necessary" within a procedure. They are:
    * - Return instruction of the function;
    * - Variable allocations;
    * - Instructions with side effects that emit outside the function itself, including:
    *   - Printing instructions;
    *   - Function params & calls, as we assume any function be necessary;
    *   - Instructions modifying memory locations that are visible outside the function.
    *   - (For simplicity) Instructions defining operands used in a conditional jump. */
    private HashSet<Instr> necessaryInstrs() {
        final HashSet<Instr> set = new HashSet<>();

        final HashSet<Instr> instrs = new HashSet<>();
        cfg.blocks.forEach(b -> instrs.addAll(b.instrs));

        // First we find the return instructions.
        instrs.forEach(i -> { if (i.op == Instr.Operator.RET) set.add(i); });
        instrs.forEach(i -> { if (i.op == Instr.Operator.ALLOC) set.add(i); });

        // Next we find the function params & calls. Printing is also included here.
        instrs.forEach(i -> { if (i.op == Instr.Operator.PARAM) set.add(i); });
        instrs.forEach(i -> { if (i.op == Instr.Operator.CALL) set.add(i); });

        // Then we find the assignments whose assigned unit is one of the following:
        // - Global variable;
        // - Function parameter of reference type;
        // - Condition operand.
        final HashSet<Var> paramRefs = new HashSet<>(cfg.funcRef.params);
        paramRefs.removeIf(v -> !v.isReference);

        final HashSet<Operand> conditions = new HashSet<>();
        for (BBlock block : cfg.blocks) {
            if (block.isCondJump) {
                conditions.add(block.condition);
            }
        }

        for (Instr instr : instrs) {
            if (!isAssign(instr)) continue;
            Operand o = assignedOperand(instr);
            if (o instanceof Var v
                    && (v.isGlobal || paramRefs.contains(v))) {
                set.add(instr);
            }
            // We're not going to do ADCE; that CDG is horrible!
            if (conditions.contains(o)) {
                set.add(instr);
            }
        }

        return set;
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

    private Operand assignedOperand(Instr instr) {
        if (!isAssign(instr)) return null;
        assert instr.res != null;
        return instr.res;
    }

    /* Now we start running a pass. First init the necessary set, and then
    * find DU & UD chains, which is later used in the process. */
    private boolean pass() {
        boolean mod = false;

        final HashSet<BBlock> bBlocks = new HashSet<>(cfg.blocks);

        /* Now we start to prepare the UdChain for all VARS & REGS. Due to the method we adopt,
        * DuChains are not required. */

        // First, find all defs within the whole CFG.
        final HashMap<Operand, HashSet<Instr>> defs = new HashMap<>();
        for (BBlock block : bBlocks) {
            for (Instr instr : block.instrs) {
                if (isAssign(instr)) {
                    Operand o = assignedOperand(instr);
                    if (!defs.containsKey(o)) defs.put(o, new HashSet<>());
                    defs.get(o).add(instr);
                }
            }
        }

        // Second, calculate GEN/KILL set for each def instr.
        // GEN is just the instr itself, so we emit it here.
        final HashMap<Instr, HashSet<Instr>> instrKill = new HashMap<>();
        for (Operand o: defs.keySet()) {
            for (Instr instr : defs.get(o)) {
                if (o instanceof Var v && (v.isArray || v.isReference)) {
                    instrKill.put(instr, new HashSet<>());
                } else {
                    // All defs of the variable other than the instr itself.
                    HashSet<Instr> killed = new HashSet<>(defs.get(o));
                    killed.remove(instr);
                    instrKill.put(instr, killed);
                }
            }
        }

        // Third, calculate GEN/KILL set for each BBlock.
        final HashMap<BBlock, HashSet<Instr>> blockGen = new HashMap<>();
        final HashMap<BBlock, HashSet<Instr>> blockKill = new HashMap<>();

        for (BBlock block : bBlocks) {
            HashSet<Instr> gen = new HashSet<>();
            for (int i = 0; i < block.instrs.size(); i++) {
                Instr instr = block.instrs.get(i);
                HashSet<Instr> g = new HashSet<>();
                if (isAssign(instr))
                    g.add(instr);
                for (int j = i + 1; j < block.instrs.size(); j++) {
                    HashSet<Instr> toRetain = instrKill.get(block.instrs.get(j));
                    if (toRetain != null)
                        g.removeAll(toRetain);
                }
                gen.addAll(g);
            }
            blockGen.put(block, gen);
        }

        for (BBlock block : bBlocks) {
            HashSet<Instr> kill = new HashSet<>();
            for (Instr instr : block.instrs) {
                if (instrKill.containsKey(instr)) {
                    kill.addAll(instrKill.get(instr));
                }
            }
            blockKill.put(block, kill);
        }

        // Fourth, calculate dataflow info for blocks.
        final HashMap<BBlock, HashSet<Instr>> RDIn = new HashMap<>();
        final HashMap<BBlock, HashSet<Instr>> RDOut = new HashMap<>();

        for (BBlock block : bBlocks) {
            RDIn.put(block, new HashSet<>());
            RDOut.put(block, new HashSet<>());
        }

        boolean m;
        do {
            m = false;
            for (BBlock block : bBlocks) {
                HashSet<Instr> set = new HashSet<>(RDIn.get(block));
                set.removeAll(blockKill.get(block));
                set.addAll(blockGen.get(block));

                m |= !set.equals(RDOut.get(block));

                RDOut.replace(block, set);

                // Update RDIn.
                for (BBlock block1 : bBlocks) {
                    HashSet<Instr> in = new HashSet<>();
                    for (BBlock block2 : block1.prevSet) {
                        if (cfg.entry == block2) continue;
                        in.addAll(RDOut.get(block2));
                    }
                    RDIn.replace(block1, in);
                }
            }
        } while (m);

        // Fifth, calculate dataflow info for each usage.
        final HashMap<Instr, HashSet<Instr>> inOfUse = new HashMap<>();
        for (BBlock block : bBlocks) {
            for (Instr instr : block.instrs) {
                if (!isUse(instr)) continue;
                final HashSet<Instr> set = new HashSet<>(RDIn.get(block));
                for (int i = 0; i < block.instrs.indexOf(instr); i++) {
                    Instr instr1 = block.instrs.get(i);
                    if (instrKill.get(instr1) != null)
                        set.removeAll(instrKill.get(instr1));
                    if (isAssign(instr1))
                        set.add(instr1);
                }
                inOfUse.put(instr, set);
            }
        }

        // Sixth, construct udChain.
        final HashMap<Operand, HashMap<Instr, HashSet<Instr>>> udChain = new HashMap<>();
        for (Instr usage : inOfUse.keySet()) {
            for (Operand used : usedOperand(usage)) {
                for (Instr def : inOfUse.get(usage)) {
                    if (assignedOperand(def).equals(used)) {
                        if (!udChain.containsKey(used)) udChain.put(used, new HashMap<>());
                        if (!udChain.get(used).containsKey(usage)) udChain.get(used).put(usage, new HashSet<>());
                        udChain.get(used).get(usage).add(def);
                    }
                }
            }
        }

        /* Now that we have the udChain constructed, we initialize the working set and
        * start spreading the usages withing the set using our udChain. */
        final HashSet<Instr> marked = necessaryInstrs();
        final Queue<Instr> queue = new ArrayDeque<>(marked);
        while (!queue.isEmpty()) {
            Instr i = queue.poll();
            for (Operand o : usedOperand(i)) {
                if (udChain.get(o) != null) {
                    final HashSet<Instr> ds = udChain.get(o).get(i);
                    if (ds != null) {
                        final HashSet<Instr> toAdd = new HashSet<>(ds);
                        toAdd.removeAll(marked);
                        queue.addAll(toAdd);
                        marked.addAll(toAdd);
                    }
                }
            }
        }

        for (BBlock block : cfg.blocks) {
            mod |= block.instrs.removeIf(instr -> !marked.contains(instr));
        }

        return mod;
    }

    /* And the same for "usages":
    - LOAD && supl == null
    - STORE
    - STREF
    - Any calculation:
      - ADD, SUB, MUL, DIV, MOD, OR, AND, MOVE;
      - LSS, LEQ, GRE, GEQ, EQL, NEQ.
    - GOIF, GONT
    - PARAM
    - RET && main != null
    */
    private boolean isUse(Instr instr) {
        return (instr.main != null && (instr.main instanceof Var || instr.main instanceof Reg))
                || (instr.supl != null && (instr.supl instanceof Var || instr.supl instanceof Reg));
    }

    private HashSet<Operand> usedOperand(Instr instr) {
        HashSet<Operand> use = new HashSet<>();

        if (instr.main != null && (instr.main instanceof Var || instr.main instanceof Reg)) { use.add(instr.main); }
        if (instr.supl != null && (instr.supl instanceof Var || instr.supl instanceof Reg)) { use.add(instr.supl); }

        return use;
    }

    /* Finally, run passes until no modification occurs in a pass. */
    public boolean run() {
        boolean mod = false, modPass;

        do {
            modPass = pass();
            mod |= modPass;
        } while (modPass);

        return mod;
    }
}
