package ir.datastruct;

import frontend.datastruct.symbol.SymFunc;
import frontend.datastruct.symbol.Symbol;
import ir.datastruct.operand.FuncRef;

import java.util.ArrayList;

public class Function implements IrStruct {
    boolean isMain = false;
    final Symbol symbol;

    // The final position of instructions. Whatever data structures are used
    // for optimization, this field must be refilled with the final instructions.
    public ArrayList<Instr> instrs = new ArrayList<>();

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

    @Override
    public ArrayList<Instr> genInstrs() {
        return instrs;
    }

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
