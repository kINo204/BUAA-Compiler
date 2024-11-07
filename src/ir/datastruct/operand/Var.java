package ir.datastruct.operand;

import datastruct.symbol.Symbol;

import static ir.datastruct.Instr.Type.i32;
import static ir.datastruct.Instr.Type.i8;

/**
 * This class can only be operand of Decl/Load/Store instructions. Direct
 * participation in calculation instructions or others are not allowed.
 */
public class Var extends Operand {
    // Backup info in Symbol object.
    public final Symbol symbol;

    // All info used for IR and code-gen should be here.
    public final String name;
    public final boolean isConstant;
    public final boolean isArray;
    public int arrayLength;

    public int frameOfs;

    public Var(Symbol symbol) {
        this.symbol = symbol;

        name = "@" + symbol.symtblId + "." + symbol.literal;

        switch (symbol.symId) {
            case Int -> {
                type = i32;
                isConstant = false;
                isArray = false;
            }
            case ConstInt -> {
                type = i32;
                isConstant = true;
                isArray = false;
            }
            case Char -> {
                type = i8;
                isConstant = false;
                isArray = false;
            }
            case ConstChar -> {
                type = i8;
                isConstant = true;
                isArray = false;
            }
            case IntArray -> {
                type = i32;
                isConstant = false;
                isArray = true;
            }
            case ConstIntArray -> {
                type = i32;
                isConstant = true;
                isArray = true;
            }
            case CharArray -> {
                type = i8;
                isConstant = false;
                isArray = true;
            }
            case ConstCharArray -> {
                type = i8;
                isConstant = true;
                isArray = true;
            }
            default -> {
                assert false;
                type = null;
                isConstant = false;
                isArray = false;
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Var var)) return false;

        return name.equals(var.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
