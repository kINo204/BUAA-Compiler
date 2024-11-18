package opt.ir;

import ir.datastruct.Instr;
import ir.datastruct.operand.Label;
import opt.ir.datastruct.BBlock;
import opt.ir.datastruct.Cfg;
import utils.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static ir.datastruct.Instr.Operator.*;

public class IrFuncOptimizer {
    private final Log log;
    private ArrayList<Instr> instrs;
    private ArrayList<Instr> optInstrs = new ArrayList<>();
    private Instr funcDefInstr;

    public IrFuncOptimizer(Log log) {
        this.log = log;
    }

    public void injectInstr(ArrayList<Instr> instrs) {
        this.instrs = instrs;
    }

    /* Optimization entry point. */
    public ArrayList<Instr> optimize() throws IOException {
        // Remove function def instr.
        funcDefInstr = instrs.remove(0);

        // 1. Generate CFG.
        while (true) {
            toBBlocks();
            toCfg();
            if (!trimCfg()) break;
            regenerateInstrs();
        }

        // 2. Call optimizers. todo

        // 3. Get optimized instrs.
        regenerateInstrs();
        while (true) {
            toBBlocks();
            toCfg();
            if (!trimCfg()) break;
            regenerateInstrs();
        }
        regenerateInstrs();
        optInstrs = instrs;
        optInstrs.add(0, funcDefInstr);
        return optInstrs;
    }

    private void initResource() {
        Label.reset();

    }

    /* Instr -> Basic Blocks */
    private final ArrayList<BBlock> bBlocks = new ArrayList<>();

    private void toBBlocks() {
        bBlocks.clear();
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
    private final BBlock
            entry = new BBlock(new ArrayList<>()),
            exit  = new BBlock(new ArrayList<>());
    private Cfg cfg;

    private void toCfg() {
        for (BBlock block : bBlocks) {
            assert !block.instrs.isEmpty();
            final Instr lastInstr = block.instrs.get(block.instrs.size() - 1);
            switch (lastInstr.op) {
                case GOTO -> {
                    // One out-edge towards GOTO target.
                    BBlock tar = blockOfInstr(((Label) lastInstr.res).target);
                    block.addNext(tar);
                }
                case GOIF, GONT -> {
                    // One out-edge towards GO** target, and another out-edge
                    // towards NEXT BBlock.
                    BBlock condTar = blockOfInstr(((Label) lastInstr.res).target);
                    block.addNext(condTar);
                    // One out-edge towards next BBlock. Same as default.
                    BBlock next = successor(block);
                    block.addNext(next);
                    // Set conditional targets.
                    block.condJump = true;
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
                }
                default -> { // The BBlock doesn't end with a jumping instr.
                    // One out-edge towards next BBlock.
                    BBlock next = successor(block);
                    block.addNext(next);
                }
            }
        }

        // Remove all labels and jumping instructions.
        // Don't delete RET instr. This is always regenerated at end of BBlock.
        for (BBlock block : bBlocks) {
            block.instrs.removeIf(instr ->
                    Arrays.asList(GOTO, GOIF, GONT, LABEL).contains(instr.op));
        }

        cfg = new Cfg(entry, exit, bBlocks);
    }

    /**
     * @return True if CFG changes during trimming.
     */
    private boolean trimCfg() {
        boolean changed = false;
        // Merging.
        // Removing empty blocks.
        // todo
        return changed;
    }

    private void regenerateInstrs() {
        initResource();
        ArrayList<Instr> regenerated = new ArrayList<>();

        for (BBlock block : bBlocks) {
            if (!block.isRet()) {
                if (!block.condJump) { // None, Goto
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
            }
        }

        for (BBlock block : bBlocks) {
            regenerated.addAll(block.instrs);
        }
        instrs = regenerated;
    }

    /* Helper functions. */
    private BBlock successor(BBlock block) {
        int ind = bBlocks.indexOf(block);
        // If this is the last block, next is exit. (Although the Validator ensures
        // in this case the last instruction is always a RET.)
        return ind < bBlocks.size() - 1 ? bBlocks.get(ind + 1) : exit;
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
