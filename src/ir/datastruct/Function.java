package ir.datastruct;

import datastruct.symbol.SymFunc;
import datastruct.symbol.Symbol;
import ir.datastruct.operand.FuncRef;
import ir.datastruct.operand.Label;

import java.util.ArrayList;

public class Function implements Value {
    boolean isMain = false;
    final Symbol symbol;
    ArrayList<Instr> instrs = new ArrayList<>();

    // For optimization.
    ArrayList<BasicBlock> basicBlocks = new ArrayList<>();
    boolean containsCall; // If no braced call, $ra may not be saved.

    public Function(SymFunc symbol) {
        this.symbol = symbol;
        FuncRef funcRef = new FuncRef(symbol);
        symbol.funcRef = funcRef;
        appendInstr(Instr.genFuncDef(funcRef));
    }

    public Function(boolean isMain) {
        assert isMain;
        this.isMain = true;
        symbol = null;
        appendInstr(Instr.genFuncDef(new FuncRef(true)));
    }

    public void appendInstr(Instr i) {
        instrs.add(i);
    }

    public ArrayList<Instr> genInstrs() {
        return instrs;
    }

    // TODO Instrs -> BasicBlock
    public void toBasicBlocks() {
        // Construct basic blocks from `this.instrs`.
        // Make flowing between bbs.
    }

    // TODO BasicBlock -> Instrs
//    public ArrayList<Instr> toInstrs() {
//        final ArrayList<Instr> instrs = new ArrayList<>();
//
//        // Re-generate jumping instrs && labels.
//        final ArrayList<Instr> jumping = new ArrayList<>(); // Index for refill.
//        for (BasicBlock b : basicBlocks) {
//            if (b.conditionValue == null) {
//                BasicBlock tar = b.nextDefault;
//                if (tar != null // an exit of the function
//                        && basicBlocks.indexOf(b) < basicBlocks.size() - 1 // not at last
//                        && tar != basicBlocks.get(basicBlocks.indexOf(b) + 1) // not going to following
//                ) {
//                    Instr j = Instr.genGoto(tar);
//                    b.instructions.add(j); // Add to tail of instrs of the block.
//                    jumping.add(j); // Add to to-refill index.
//                }
//            } else {
//                BasicBlock tar = b.nextTrue;
//                if (tar != null // an exit of the function
//                        && basicBlocks.indexOf(b) < basicBlocks.size() - 1 // not at last
//                        && tar != basicBlocks.get(basicBlocks.indexOf(b) + 1) // not going to following
//                ) {
//                    Instr j = Instr.genGoif(tar, b.conditionValue);
//                    b.instructions.add(j);
//                    jumping.add(j);
//                }
//
//                tar = b.nextFalse;
//                if (tar != null // an exit of the function
//                        && basicBlocks.indexOf(b) < basicBlocks.size() - 1 // not at last
//                        && tar != basicBlocks.get(basicBlocks.indexOf(b) + 1) // not going to following
//                ) {
//                    Instr j = Instr.genGoto(tar);
//                    b.instructions.add(j);
//                    jumping.add(j);
//                }
//            }
//        }
//        // Do refill: replace Block target by Instr target.
//        for (Instr j : jumping) {
//            Instr tar = ((BasicBlock) j.main).getEntry();
//            j.main = new Label(tar);
//        }
//
//        for (BasicBlock b : basicBlocks) {
//            instrs.addAll(b.genInstrs());
//        }
//        return instrs;
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Instr i : genInstrs()) {
            sb.append(i.toString()).append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
