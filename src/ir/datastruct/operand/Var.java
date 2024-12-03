package ir.datastruct.operand;

import frontend.datastruct.symbol.SymConstVar;
import frontend.datastruct.symbol.SymVar;
import frontend.datastruct.symbol.Symbol;
import mips.datastruct.MipsOperand;

import static ir.datastruct.Instr.Type.i32;
import static ir.datastruct.Instr.Type.i8;

/**
 * This class can only be operand of Decl/Load/Store instructions. Direct
 * participation in calculation instructions or others are not allowed.
 */
public class Var extends Operand
        implements MipsOperand/* Only used for var name in global decl */
{
    // Backup info in Symbol object.
    public final Symbol symbol;

    // All info used for IR and code-gen should be here.
    public final String name;
    public final boolean isReference;
    public final boolean isConstant;
    public boolean isGlobal = false;
    public final boolean isArray;
    public int arrayLength = 0;

    // Compiler generated.
    public static Var compilerTempVar(String name) {
        Var v = new Var(new SymVar(name));
        v.isGlobal = true;
        return v;
    }

    public Var(Symbol symbol) {
        this.symbol = symbol;

        name = symbol.literal + "_" + symbol.symtblId;


        if (symbol instanceof SymConstVar var) {
            isReference = false;
            isArray = var.isArray;
        } else {
            isReference = ((SymVar) symbol).isReference;
            isArray = ((SymVar) symbol).isArray;
        }

        switch (symbol.symId) {
            case Int, IntArray -> {
                type = i32;
                isConstant = false;
            }
            case ConstInt, ConstIntArray -> {
                type = i32;
                isConstant = true;
            }
            case Char, CharArray -> {
                type = i8;
                isConstant = false;
            }
            case ConstChar, ConstCharArray -> {
                type = i8;
                isConstant = true;
            }
            default -> {
                assert false;
                type = null;
                isConstant = false;
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
