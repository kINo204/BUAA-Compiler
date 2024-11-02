package ir.datastruct;

import datastruct.symbol.Symbol;
import ir.datastruct.operand.Label;

import java.util.ArrayList;

class Function implements Value {
    boolean isMain = false;
    final Symbol symbol;
    ArrayList<BasicBlock> basicBlocks = new ArrayList<>();

    private BasicBlock curBasicBlock = null;

    Function(Symbol symbol) {
        this.symbol = symbol;
    }

    Function(boolean isMain) {
        assert isMain;
        this.isMain = true;
        symbol = null;
    }

    void createBasicBlock() {
        curBasicBlock = new BasicBlock();
        basicBlocks.add(curBasicBlock);
    }

    void appendInstr(Instr i) {
        curBasicBlock.instructions.add(i);
    }

    public ArrayList<Instr> genInstrs() {
        final ArrayList<Instr> instrs = new ArrayList<>();

        // Add jumping instrs.
        final ArrayList<Instr> jumping = new ArrayList<>(); // Index for refill.
        for (BasicBlock b : basicBlocks) {
            if (b.conditionValue == null) {
                BasicBlock tar = b.nextDefault;
                if (tar != null // an exit of the function
                        && basicBlocks.indexOf(b) < basicBlocks.size() - 1 // not at last
                        && tar != basicBlocks.get(basicBlocks.indexOf(b) + 1) // not going to following
                ) {
                    Instr j = Instr.genGoto(tar);
                    b.instructions.add(j); // Add to tail of instrs of the block.
                    jumping.add(j); // Add to to-refill index.
                }
            } else {
                BasicBlock tar = b.nextTrue;
                if (tar != null // an exit of the function
                        && basicBlocks.indexOf(b) < basicBlocks.size() - 1 // not at last
                        && tar != basicBlocks.get(basicBlocks.indexOf(b) + 1) // not going to following
                ) {
                    Instr j = Instr.genGoif(tar, b.conditionValue);
                    b.instructions.add(j);
                    jumping.add(j);
                }

                tar = b.nextFalse;
                if (tar != null // an exit of the function
                        && basicBlocks.indexOf(b) < basicBlocks.size() - 1 // not at last
                        && tar != basicBlocks.get(basicBlocks.indexOf(b) + 1) // not going to following
                ) {
                    Instr j = Instr.genGoto(tar);
                    b.instructions.add(j);
                    jumping.add(j);
                }
            }
        }
        // Do refill: replace Block target by Instr target.
        for (Instr j : jumping) {
            Instr tar = ((BasicBlock) j.main).getEntry();
            j.main = new Label(tar);
        }

        for (BasicBlock b : basicBlocks) {
            instrs.addAll(b.genInstrs());
        }
        return instrs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isMain) {
            sb.append("fun main:\n");
        } else {
            assert symbol != null;
            sb.append("fun ").append(symbol.literal).append(":\n");
        }
        for (Instr i : genInstrs()) {
            sb.append("\t").append(i.toString()).append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
