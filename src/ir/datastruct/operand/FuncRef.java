package ir.datastruct.operand;

import frontend.datastruct.ast.AstFuncFParam;
import frontend.datastruct.ast.Token;
import frontend.datastruct.symbol.SymFunc;
import frontend.datastruct.symbol.SymVar;
import frontend.datastruct.symbol.Symbol;
import ir.datastruct.Instr;
import mips.datastruct.MipsOperand;
import mips.datastruct.MipsReg;

import java.util.ArrayList;
import java.util.HashSet;

import static ir.datastruct.Instr.Type.*;

public class FuncRef extends Operand implements MipsOperand {

    public String funcName;
    private int symtblId;

    public final ArrayList<Var> params = new ArrayList<>();

    public final HashSet<MipsReg> allocated = new HashSet<>();

    public FuncRef(SymFunc symbol) {
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

    private FuncRef() { }

    public static FuncRef frGetint() {
        FuncRef funcRef = new FuncRef();
        funcRef.type = i32;
        funcRef.funcName = "getint";
        funcRef.symtblId = 1;
        return funcRef;
    }

    public static FuncRef frGetchar() {
        FuncRef funcRef = new FuncRef();
        funcRef.type = i8;
        funcRef.funcName = "getchar";
        funcRef.symtblId = 1;
        return funcRef;
    }

    public static FuncRef frPutchar() {
        FuncRef funcRef = new FuncRef();
        funcRef.type = VOID;
        funcRef.funcName = "putchar";
        funcRef.symtblId = 1;

        AstFuncFParam funcFParam = new AstFuncFParam();
        funcFParam.type = new Token(Token.TokenId.CHARTK, "char", -1);
        funcFParam.ident = new Token(Token.TokenId.IDENFR, "ch", -1);
        funcFParam.isArray = false;

        funcRef.params.add(new Var(
                new SymVar(funcFParam)
        ));
        return funcRef;
    }

    public static FuncRef frPutint() {
        FuncRef funcRef = new FuncRef();
        funcRef.type = VOID;
        funcRef.funcName = "putint";
        funcRef.symtblId = 1;

        AstFuncFParam funcFParam = new AstFuncFParam();
        funcFParam.type = new Token(Token.TokenId.INTTK, "int", -1);
        funcFParam.ident = new Token(Token.TokenId.IDENFR, "n", -1);
        funcFParam.isArray = false;

        funcRef.params.add(new Var(
                new SymVar(funcFParam)
        ));
        return funcRef;
    }

    public FuncRef(boolean isMain) {
        assert isMain;
        funcName = "main";
        symtblId = 1;
        super.type = i32;
    }

    @Override
    public String toString() {
        return funcName;
    }
}
