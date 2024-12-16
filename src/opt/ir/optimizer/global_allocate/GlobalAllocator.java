package opt.ir.optimizer.global_allocate;

import ir.datastruct.Instr;
import ir.datastruct.operand.Const;
import ir.datastruct.operand.Var;
import mips.datastruct.MipsReg;
import opt.ir.datastruct.BBlock;
import opt.ir.datastruct.Cfg;
import opt.ir.datastruct.Net;

import java.util.*;

import static mips.datastruct.MipsReg.RegId.*;
import static mips.datastruct.MipsReg.r;

public class GlobalAllocator {
    private final Cfg cfg;

    public GlobalAllocator(Cfg cfg) {
        this.cfg = cfg;
    }

    private final HashMap<Net, MipsReg> allocation = new HashMap<>();

    /* Before any analysis, we need to know which IR instructions
    are "definition"s. Look at `IrMaker` and we found only one:
    - STORE && supl == null

    We benefit from normal calculations writing their results into Regs.
     */
    private boolean isDefine(Instr instr) {
        if (instr.op == Instr.Operator.ALLOC) return false;
        if (instr.op == Instr.Operator.STREF) return false;
        return instr.res instanceof Var;
    }

    private Var varDefined(Instr instr) {
        if (instr.res instanceof Var v) return v;
        else return null;
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
        return instr.main instanceof Var || instr.supl instanceof Var
                || instr.op == Instr.Operator.STREF;
    }

    private HashSet<Var> varUsed(Instr instr) {
        HashSet<Var> use = new HashSet<>();

        if (instr.main instanceof Var v) { use.add(v); }
        if (instr.supl instanceof Var v) { use.add(v); }

        if (instr.op == Instr.Operator.STREF) use.add((Var) instr.res);

        return use;
    }

    public HashMap<Net, MipsReg> run() {
        for (Var var : cfg.funcRef.params) {
            cfg.entry.instrs.add(Instr.genMove(var, new Const(0)));
        }

        final ArrayList<BBlock> bBlocks = new ArrayList<>(cfg.blocks);
        bBlocks.add(0, cfg.entry);

        // First, find all defs within the whole CFG.
        final HashMap<Var, HashSet<Instr>> defs = new HashMap<>();
        for (BBlock block : bBlocks) {
            for (Instr instr : block.instrs) {
                if (isDefine(instr)) {
                    Var v = varDefined(instr);
                    if (!defs.containsKey(v)) defs.put(v, new HashSet<>());
                    defs.get(v).add(instr);
                }
            }
        }

        // Second, calculate GEN/KILL set for each def instr.
        // GEN is just the instr itself, so we emit it here.
        final HashMap<Instr, HashSet<Instr>> instrKill = new HashMap<>();

        for (Var v: defs.keySet()) {
            for (Instr instr : defs.get(v)) {
                // All defs of the variable other than the instr itself.
                HashSet<Instr> killed = new HashSet<>(defs.get(v));
                killed.remove(instr);
                instrKill.put(instr, killed);
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
                if (isDefine(instr))
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

        boolean mod;
        do {
            mod = false;
            for (BBlock block : bBlocks) {
                HashSet<Instr> set = new HashSet<>(RDIn.get(block));
                set.removeAll(blockKill.get(block));
                set.addAll(blockGen.get(block));

                mod |= !set.equals(RDOut.get(block));

                RDOut.replace(block, set);

                // Update RDIn.
                for (BBlock block1 : bBlocks) {
                    HashSet<Instr> in = new HashSet<>();
                    for (BBlock block2 : block1.prevSet) {
                        in.addAll(RDOut.get(block2));
                    }
                    RDIn.replace(block1, in);
                }
            }
        } while (mod);

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
                    if (isDefine(instr1))
                        set.add(instr1);
                }
                inOfUse.put(instr, set);
            }
        }

        // Sixth, construct DuChain.
        final HashMap<Var, HashMap<Instr, HashSet<Instr>>> duChain = new HashMap<>();
        for (Var v : defs.keySet()) {
            duChain.put(v, new HashMap<>());
            for (Instr defInstr : defs.get(v)) {
                duChain.get(v).put(defInstr, new HashSet<>());
            }
        }

        for (Instr useInstr : inOfUse.keySet()) {
            HashSet<Var> used = varUsed(useInstr);
            for (Instr defReached : inOfUse.get(useInstr)) {
                if (used.contains(varDefined(defReached))) {
                    duChain.get(varDefined(defReached)).get(defReached).add(useInstr);
                }
            }
        }

        // Seventh, construct DuNet.
        final HashMap<Var, HashSet<Net>> duNets = new HashMap<>();

        for (Var var : duChain.keySet()) {
            final HashMap<HashSet<Instr>, HashSet<Instr>> curNet = new HashMap<>();

            for (Instr def : duChain.get(var).keySet()) {
                boolean merge = false;
                final HashSet<HashSet<Instr>> toMerge = new HashSet<>();
                for (HashSet<Instr> chain : curNet.keySet()) {
                    final HashSet<Instr> t = new HashSet<>(curNet.get(chain));
                    t.retainAll(duChain.get(var).get(def));
                    if (!t.isEmpty()) {
                        toMerge.add(chain);
                        merge = true;
                    }
                }
                if (merge) {
                    final HashSet<Instr> resDef = new HashSet<>(List.of(def));
                    final HashSet<Instr> resUse = new HashSet<>(duChain.get(var).get(def));
                    toMerge.forEach(resDef::addAll);
                    toMerge.forEach(ds -> resUse.addAll(curNet.get(ds)));
                    toMerge.forEach(curNet::remove);
                    curNet.put(resDef, resUse);
                } else {
                    curNet.put(new HashSet<>(List.of(def)), duChain.get(var).get(def));
                }
            }

            // Construct Nets of var.
            duNets.put(var, new HashSet<>());
            for (Map.Entry<HashSet<Instr>, HashSet<Instr>> entry : curNet.entrySet()) {
                duNets.get(var).add(
                        new Net(var, entry.getKey(), entry.getValue()));
            }
        }

        // We don't use net for arrays yet.
        duNets.keySet().removeIf(v -> v.isArray || v.isReference || v.isGlobal);

        // Eighth, calculate liveliness of Nets. Calculate Def and Use for blocks, and perform
        // dataflow analysis.
        final HashMap<BBlock, HashSet<Net>> LVDef = new HashMap<>();
        final HashMap<BBlock, HashSet<Net>> LVUse = new HashMap<>();
        for (BBlock block : bBlocks) {
            LVDef.put(block, new HashSet<>());
            LVUse.put(block, new HashSet<>());
        }

        ArrayList<Net> nets = new ArrayList<>();
        for (Var v : duNets.keySet()) {
            for (Net net : duNets.get(v)) {
                nets.add(net);
                final HashMap<BBlock, Instr> earliestDefs = new HashMap<>();
                for (Instr def : net.defs) {
                    BBlock block = cfg.blockOfInstr(def);
                    if (earliestDefs.containsKey(block)) {
                        Instr prevDef = earliestDefs.get(block);
                        if (block.instrs.indexOf(def) < block.instrs.indexOf(prevDef)) {
                            earliestDefs.replace(block, def);
                        }
                    } else if (block != null) {
                        earliestDefs.put(block, def);
                    }
                }

                final HashMap<BBlock, Instr> earliestUses = new HashMap<>();
                for (Instr use : net.usages) {
                    BBlock block = cfg.blockOfInstr(use);
                    if (earliestUses.containsKey(block)) {
                        Instr prevUse = earliestUses.get(block);
                        if (block.instrs.indexOf(use) < block.instrs.indexOf(prevUse)) {
                            earliestUses.replace(block, use);
                        }
                    } else if (block != null) {
                        earliestUses.put(block, use);
                    }
                }

                final HashSet<BBlock> blocks = new HashSet<>(earliestDefs.keySet());
                blocks.addAll(earliestUses.keySet());
                for (BBlock block : blocks) {
                    final boolean isDef = earliestDefs.containsKey(block),
                            isUse = earliestUses.containsKey(block);
                    assert isDef || isUse;
                    if (isDef && !isUse) {
                        LVDef.get(block).add(net);
                    } else if (!isDef) {
                        LVUse.get(block).add(net);
                    } else {
                        int indDef = block.instrs.indexOf(earliestDefs.get(block));
                        int indUse = block.instrs.indexOf(earliestUses.get(block));
                        if (indUse <= indDef) {
                            LVUse.get(block).add(net);
                        } else {
                            LVDef.get(block).add(net);
                        }
                    }
                }
            }
        }

        final HashMap<BBlock, HashSet<Net>> LVIn = new HashMap<>();
        final HashMap<BBlock, HashSet<Net>> LVOut = new HashMap<>();
        for (BBlock block : bBlocks) {
            LVIn.put(block, new HashSet<>());
            LVOut.put(block, new HashSet<>());
        }

        do {
            mod = false;
            for (int i = bBlocks.size() - 1; i >= 0; i--) {
                BBlock block = bBlocks.get(i);
                final HashSet<Net> set = new HashSet<>(LVOut.get(block));
                set.removeAll(LVDef.get(block));
                set.addAll(LVUse.get(block));

                mod |= !set.equals(LVIn.get(block));

                LVIn.replace(block, set);

                // Update LVOut.
                for (BBlock block1 : bBlocks) {
                    if (block1.nextSet.isEmpty()) continue;
                    HashSet<Net> out = new HashSet<>();
                    for (BBlock block2 : block1.nextSet) {
                        if (block2 == cfg.exit) continue;
                        out.addAll(LVIn.get(block2));
                    }
                    LVOut.replace(block1, out);
                }
            }
        } while (mod);

        // Setup var -> net reference.
        for (Net net : nets) {
            for (Instr def : net.defs) {
                ((Var) def.res).net = net;
            }
            for (Instr use : net.usages) {
                if (use.main instanceof Var v && v.equals(net.var)) {
                    v.net = net;
                }
                if (use.supl instanceof Var v && v.equals(net.var)) {
                    v.net = net;
                }
                if (use.op == Instr.Operator.STREF && use.res instanceof Var v && v.equals(net.var)) {
                    v.net = net;
                }
            }
        }

        // Calculate conflictsMat.
        int nNets = nets.size();
        boolean[][] conflictsMat = new boolean[nNets][nNets];
        for (int i = 0; i < nNets; i++) {
            for (int j = 0; j < nNets; j++) {
                conflictsMat[i][j] = false;
            }
        }

        for (int i = 0; i < nNets - 1; i++) {
            next:
            for (int j = i + 1; j < nNets; j++) {
                Net n1 = nets.get(i);
                Net n2 = nets.get(j);

                // Calculate LV of n2 at each def of n1.
                for (Instr def : n1.defs) {
                    BBlock block = cfg.blockOfInstr(def);
                    boolean lvN2 = LVOut.get(block).contains(n2);
                    for (int k = block.instrs.size() - 1; k >/* todo Is b = a conflict? */ block.instrs.indexOf(def); k--) {
                        Instr instr = block.instrs.get(k);
                        if (n2.defs.contains(instr)) lvN2 = false;
                        if (n2.usages.contains(instr)) lvN2 = true;
                    }
                    if (lvN2) {
                        conflictsMat[i][j] = true;
                        conflictsMat[j][i] = true;
                        continue next;
                    }
                }

                // Calculate LV of n1 at each def of n2.
                for (Instr def : n2.defs) {
                    BBlock block = cfg.blockOfInstr(def);
                    boolean lvN1 = LVOut.get(block).contains(n1);
                    for (int k = block.instrs.size() - 1; k >/* todo Is b = a conflict? */ block.instrs.indexOf(def); k--) {
                        Instr instr = block.instrs.get(k);
                        if (n1.defs.contains(instr)) lvN1 = false;
                        if (n1.usages.contains(instr)) lvN1 = true;
                    }
                    if (lvN1) {
                        conflictsMat[i][j] = true;
                        conflictsMat[j][i] = true;
                        continue next;
                    }
                }
            }
        }

        if (false) {
            for (Var v : duNets.keySet()) {
                for (Net net : duNets.get(v)) {
                    System.out.print(nets.indexOf(net) + "=");
                    System.out.println(net);
                }
            }

            System.out.print("i\\j\t");
            for (int i = 0; i < nNets; i++) {
                System.out.print(i + "   \t");
            }
            System.out.println();
            for (int i = 0; i < nNets; i++) {
                System.out.print(i + "\t");
                for (int j = 0; j < nNets; j++) {
                    System.out.print(conflictsMat[i][j] + "\t");
                }
                System.out.println();
        }
        }

        final HashMap<Net, HashSet<Net>> conflictsList = new HashMap<>();
        for (Net net : nets) {
            conflictsList.put(net, new HashSet<>());
            for (int j = 0; j < nNets; j++) {
                if (conflictsMat[nets.indexOf(net)][j]) {
                    conflictsList.get(net).add(nets.get(j));
                }
            }
        }

        final HashSet<Net> nodes = new HashSet<>(conflictsList.keySet());
        final Stack<Net> stack = new Stack<>();
        while (!nodes.isEmpty()) {
            boolean success = false;
            for (Net net : nets) {
                if (!nodes.contains(net)) continue; // Actually traversing all `nodes` here.
                int nConflicts = 0;
                for (Net neighbour : conflictsList.get(net)) {
                    if (nodes.contains(neighbour)) { nConflicts++; }
                }
                if (nConflicts < nRegs) {
                    success = true;
                    nodes.remove(net);
                    stack.push(net);
                    break;
                }
            }
            if (!success) {
                // Abandon any one of the nets to alloc.
                int ind = (int) (nodes.size() * Math.random());
                Net toRemove = (Net) nodes.toArray()[ind];
                nodes.remove(toRemove);
                // Remove this node from the `conflictsList` as well.
                for (Net neighbour : conflictsList.get(toRemove)) {
                    conflictsList.get(neighbour).remove(toRemove);
                }
                conflictsList.remove(toRemove);
            }
        }

        while (!stack.isEmpty()) {
            Net netToColor = stack.pop();
            final ArrayList<MipsReg> color = new ArrayList<>(availableRegs);
            HashSet<Net> neighbours = conflictsList.get(netToColor);
            for (Net neighbour : neighbours) {
                if (allocation.containsKey(neighbour)) {
                    color.remove(allocation.get(neighbour));
                }
            }
            assert !color.isEmpty();
            allocation.put(netToColor, (MipsReg) color.toArray()[0]);
        }

        if (false) {
            for (Net net : allocation.keySet()) {
                MipsReg globReg = allocation.get(net);
                System.out.printf("%s <=> %s\n", net , globReg);
            }
        }

        cfg.entry.instrs.clear();

        return allocation;
    }

    private final ArrayList<MipsReg> availableRegs = new ArrayList<>(List.of(
            r(s0), r(s1), r(s2), r(s3), r(s4), r(s5), r(s6), r(s7)
    ));

    private final int nRegs = availableRegs.size();
}