package ir.datastruct.operand;

import datastruct.symbol.SymFunc;
import datastruct.symbol.SymVar;
import datastruct.symbol.Symbol;
import ir.datastruct.Instr;
import mips.datastruct.MipsOperand;

import java.util.ArrayList;

import static ir.datastruct.Instr.Type.*;

public class FuncRef extends Operand implements MipsOperand {
    // Backup info in symbol.
    private final SymFunc symbol;

    public final String funcName;
    private final int symtblId;

    public final ArrayList<Var> params = new ArrayList<>();

    public FuncRef(SymFunc symbol) {
        this.symbol = symbol;
        funcName = symbol.literal;
        symtblId = symbol.symtblId;

        super.type = switch (symbol.symId) {
            case IntFunc -> i32;
            case CharFunc -> i8;
            case VoidFunc -> VOID;
            default -> null;
        };

        for (Symbol param : symbol.params) {
            Var varParam = new Var(param);
            params.add(varParam);
            ((SymVar) param).irVar = varParam;
            varParam.arrayLength = -1; // This is infinity.
        }
    }

    public FuncRef(boolean isMain) {
        assert isMain;
        this.symbol = null;
        funcName = "main";
        symtblId = 1;
        super.type = i32;
    }

    @Override
    public String toString() {
        return funcName;
    }
}
