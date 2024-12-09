package opt.ir;

import ir.datastruct.Instr;
import ir.datastruct.Ir;
import ir.datastruct.operand.*;
import mips.datastruct.MipsReg;
import opt.ir.datastruct.BBlock;
import opt.ir.datastruct.Cfg;
import opt.ir.datastruct.Net;
import opt.ir.optimizer.TailRecursionEliminator;
import opt.ir.optimizer.global_allocate.GlobalAllocator;
import utils.Log;

import java.io.IOException;
import java.util.*;

import static ir.datastruct.Instr.Operator.*;
import static ir.datastruct.Instr.Type.*;

public class IrFuncOptimizer {
    private final Ir ir;
    private final Log log;
    private ArrayList<Instr> instrs;
    private ArrayList<Instr> optInstrs = new ArrayList<>();
    private final GlobalAlloc globalAlloc;
    private Instr funcDefInstr;
    private String funcName;
    private int labelStartingInd;

    public IrFuncOptimizer(Ir ir, GlobalAlloc globalAlloc, Log log) {
        this.ir = ir;
        this.log = log;
        this.globalAlloc = globalAlloc;
    }

    public void injectInstr(ArrayList<Instr> instrs) {
        this.instrs = instrs;
    }

    /* Optimization entry point. */
    public ArrayList<Instr> optimize() throws IOException {
        // Remove function def instr.
        funcDefInstr = instrs.remove(0);
        funcName = ((FuncRef) funcDefInstr.res).funcName;
        labelStartingInd = Label.getInd();

        // Generate CFG.
        toBBlocks();
        toCfg();
        trimCfg();
        regenerateInstrs(false); // Regenerate once here to eliminate following block fragments.
        toBBlocks();
        toCfg();
        trimCfg();

        // Call optimizers on CFG. todo

        HashMap<Net, MipsReg> allocation = new GlobalAllocator(cfg).run();
        globalAlloc.addAllocations(allocation);
        ((FuncRef) funcDefInstr.res).allocated.addAll(allocation.values());

        new TailRecursionEliminator(cfg).run();

        // Regenerate instructions from CFG.
        regenerateInstrs(false);
        toBBlocks();
        toCfg();
        trimCfg();

        log.println(cfg); // Print CFG here.

        regenerateInstrs(true);
        optInstrs = instrs;
        optInstrs.add(0, funcDefInstr);
        return optInstrs;
    }

    private void initResource() {
        Label.resetInd(labelStartingInd);
    }

    /* Instr -> Basic Blocks */
    private final ArrayList<BBlock> bBlocks = new ArrayList<>();

    private void toBBlocks() {
        bBlocks.clear();
        BBlock.count = 0;
        assert !instrs.isEmpty();

        // Find the leader instrs:
        // 1. the starting instruction
        // 2. every target instruction of a jump
        // 3. every instruction right following a jump
        // * Function calls not included! (We are in-function optimizer)
        final HashSet<Instr> leaders = new HashSet<>();
        leaders.add(instrs.get(0));
        for (Instr i : instrs) {
            if (isJumpInstr(i)) {
                // Target instruction.
                if (i.op != RET) {
                    Label targetLabel = (Label) i.res;
                    assert targetLabel.target != null;
                    leaders.add(targetLabel.target);
                }
                // Next instruction.
                int indNext = instrs.indexOf(i) + 1;
                if (indNext < instrs.size()) {
                    leaders.add(instrs.get(indNext));
                }
            }
        }

        final ArrayList<Integer> leaderNos = new ArrayList<>();
        for (Instr i : leaders) {
            leaderNos.add(instrs.indexOf(i));
        }
        leaderNos.sort(Integer::compareTo);

        // Use leader indexes to extract instrs for blocks.
        for (int i = 0; i < leaderNos.size() - 1; i++) {
            final int thisLeaderNo = leaderNos.get(i);
            final int nextLeaderNo = leaderNos.get(i + 1);
            ArrayList<Instr> blockInstrs = new ArrayList<>(instrs.subList(thisLeaderNo, nextLeaderNo));
            bBlocks.add(new BBlock(blockInstrs));
        }
        // Tha last block.
        final int thisLeaderNo = leaderNos.get(leaderNos.size() - 1);
        final int nextLeaderNo = instrs.size(); // last index + 1
        bBlocks.add(new BBlock(
                new ArrayList<>(instrs.subList(thisLeaderNo, nextLeaderNo))));
    }

    /* BBlocks -> CFG */
    private Cfg cfg;

    private void toCfg() {
        BBlock entry = new BBlock(new ArrayList<>());
        BBlock exit = new BBlock(new ArrayList<>());
        if (!bBlocks.isEmpty()) {
            BBlock first = bBlocks.get(0);
            entry.addNext(first);
            first.addPrev(entry);
        }
        for (BBlock block : bBlocks) {
            assert !block.instrs.isEmpty();
            final Instr lastInstr = block.instrs.get(block.instrs.size() - 1);
            switch (lastInstr.op) {
                case GOTO -> {
                    // One out-edge towards GOTO target.
                    BBlock tar = blockOfInstr(((Label) lastInstr.res).target);
                    block.addNext(tar);
                    tar.addPrev(block);
                }
                case GOIF, GONT -> {
                    // One out-edge towards GO** target, and another out-edge
                    // towards NEXT BBlock.
                    BBlock condTar = blockOfInstr(((Label) lastInstr.res).target);
                    block.addNext(condTar);
                    condTar.addPrev(block);
                    // One out-edge towards next BBlock. Same as default.
                    BBlock next = successor(block);
                    block.addNext(next);
                    next.addPrev(block);
                    // Set conditional targets.
                    block.isCondJump = true;
                    block.condition = lastInstr.main;
                    if (lastInstr.op == GOIF) {
                        block.tarTrue = condTar;
                        block.tarFalse = next;
                    } else {
                        block.tarFalse = condTar;
                        block.tarTrue = next;
                    }
                }
                case RET -> {
                    // This one is simple: one out-edge towards exit.
                    block.addNext(exit);
                    exit.addPrev(block);
                }
                default -> { // The BBlock doesn't end with a jumping instr.
                    // One out-edge towards next BBlock.
                    BBlock next = successor(block);
                    block.addNext(next);
                    next.addPrev(block);
                }
            }
        }

        // Remove all labels and jumping instructions.
        // Don't delete RET instr. This is always regenerated at end of BBlock.
        for (BBlock block : bBlocks) {
            block.instrs.removeIf(instr ->
                    Arrays.asList(GOTO, GOIF, GONT, LABEL).contains(instr.op));
        }

        cfg = new Cfg(entry, exit, bBlocks, funcDefInstr);
    }

    /**
     * @return True if CFG changes during trimming.
     */
    private void trimCfg() {
        boolean changed;
        do {
            // Removing unreachable blocks.
            Iterator<BBlock> iterator = cfg.blocks.iterator();
            while (iterator.hasNext()) {
                BBlock block = iterator.next();
                if (block.prevSet.isEmpty()) {
                    for (BBlock successor : block.nextSet) {
                        cfg.disconnect(block, successor);
                    }
                    iterator.remove();
                }
            }

            // Removing empty blocks.
            changed = false;
            ArrayList<BBlock> blocks = new ArrayList<>(cfg.blocks);
            blocks.add(0, cfg.entry);
            for (BBlock block : blocks) {
                if (!block.isCondJump) {
                    assert block.nextSet.size() == 1;
                    BBlock next = (BBlock) block.nextSet.toArray()[0];
                    if (next.isEmpty() && !next.isCondJump && next != cfg.exit) {
                        BBlock newNext = (BBlock) next.nextSet.toArray()[0];
                        changed = next != newNext;
                        cfg.disconnect(block, next);
                        cfg.connect(block, newNext);
                    }
                } else {
                    if (block.tarTrue.isEmpty() && !block.tarTrue.isCondJump && block.tarTrue != cfg.exit) {
                        BBlock next = block.tarTrue;
                        BBlock newNext = (BBlock) next.nextSet.toArray()[0];
                        changed = next != newNext;
                        cfg.disconnectTrue(block, next);
                        cfg.connectTrue(block, newNext);
                    }
                    if (block.tarFalse.isEmpty() && !block.tarFalse.isCondJump && block.tarFalse != cfg.exit) {
                        BBlock next = block.tarFalse;
                        BBlock newNext = (BBlock) next.nextSet.toArray()[0];
                        changed = next != newNext;
                        cfg.disconnectFalse(block, next);
                        cfg.connectFalse(block, newNext);
                    }
                }
            }
        } while (changed);
    }

    private void regenerateInstrs(boolean isFinalGeneration) {
        initResource();
        ArrayList<Instr> regenerated = new ArrayList<>();

        // Generate profiler vars global decl.
        if (IrOptUtils.genBlockExCounter && isFinalGeneration) {
            for (BBlock block : bBlocks) {
                Var tVar = Var.compilerTempVar(funcName + "_B" + block.id);
                IrOptUtils.blockProfilerVars.add(tVar);
                ir.frontInstrs.add(Instr.genGlobDecl(tVar, new GlobInitVals(new Const(0))));
            }
        }

        for (BBlock block : bBlocks) {
            if (!block.isRet()) {
                if (!block.isCondJump) { // None, Goto
                    assert block.nextSet.size() == 1;
                    if (block.nextSet.toArray()[0] != successor(block)) {
                        block.instrs.add(Instr.genGoto(
                                labelBlock((BBlock) block.nextSet.toArray()[0])));
                    }
                } else { // Goif, Gont
                    BBlock successor = successor(block);
                    if (block.tarTrue == successor) {
                        block.instrs.add(Instr.genGoIfNot(
                                labelBlock(block.tarFalse), block.condition));
                    } else if (block.tarFalse == successor) {
                        block.instrs.add(Instr.genGoif(
                                labelBlock(block.tarTrue), block.condition));
                    } else {
                        block.instrs.add(Instr.genGoif(
                                labelBlock(block.tarTrue), block.condition));
                        block.instrs.add(Instr.genGoto(
                                labelBlock(block.tarFalse)));
                    }
                }
            } else {
                if (funcName.equals("main") && IrOptUtils.genBlockExCounter && isFinalGeneration) {
                    Instr ret = block.instrs.remove(block.instrs.size() - 1);
                    block.instrs.addAll(IrOptUtils.cPrintln("\nProfile info:"));
                    for (Var profileVar : IrOptUtils.blockProfilerVars) {
                        block.instrs.addAll(IrOptUtils.cPrint(profileVar.name.substring(0, profileVar.name.length() - 2)));
                        block.instrs.addAll(IrOptUtils.cPrint("\t"));
                        block.instrs.addAll(IrOptUtils.cPrintln(profileVar));
                    }
                    block.instrs.add(ret);
                }
            }
        }

        for (BBlock block : bBlocks) {
            if (block.instrs.get(0).op == LABEL) {
                regenerated.add(block.instrs.remove(0));
            }

            // Generate profiler var addition.
            if (IrOptUtils.genBlockExCounter && isFinalGeneration) {
                Var tVar = Var.compilerTempVar(funcName + "_B" + block.id);
                Reg res = new Reg(i32);
                regenerated.add(Instr.genCalc(ADD, res, tVar, new Const(1)));
                regenerated.add(Instr.genStore(tVar, res));

            }

            regenerated.addAll(block.instrs);
        }
        instrs = regenerated;
    }

    /* Helper functions. */
    private BBlock successor(BBlock block) {
        int ind = bBlocks.indexOf(block);
        // If this is the last block, next is exit. (Although the Validator ensures
        // in this case the last instruction is always a RET.)
        return ind < bBlocks.size() - 1 ? bBlocks.get(ind + 1) : cfg.exit;
    }

    /**
     * Ensure a label exists at the start of a BBlock.
     * @param target BBlock to look at.
     * @return The label of the BBlock.
     */
    private Label labelBlock(BBlock target) {
        if (!target.instrs.isEmpty()
                && target.instrs.get(0).op == LABEL) {
            return (Label) target.instrs.get(0).res;
        } else {
            Label label = new Label();
            Instr labelDecl = Instr.genLabelDecl(label);
            target.instrs.add(0, labelDecl);
            return label;
        }
    }

    private boolean isJumpInstr(Instr i) {
        // (including RET instr)
        return Arrays.asList(GOTO, GOIF, GONT, RET)
                .contains(i.op);
    }

    private BBlock blockOfInstr(Instr i) {
        BBlock searching = null;
        for (BBlock block : bBlocks) {
            if (block.instrs.contains(i)) {
                searching = block;
                break;
            }
        }
        return searching;
    }
}
