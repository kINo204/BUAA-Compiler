package ir.datastruct.operand;

import datastruct.symbol.SymFunc;

import static ir.datastruct.Instr.Type.*;

public class FuncRef extends Operand {
    // Backup info in symbol.
    private final SymFunc symbol;

    private final String funcName;
    private final int symtblId;

    // TODO Params info:

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

        // TODO Create Vars for params.
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
        return String.format("@%s.%s", symtblId, funcName);
    }
}
