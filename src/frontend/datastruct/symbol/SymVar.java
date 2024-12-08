package frontend.datastruct.symbol;

import frontend.datastruct.ast.AstFuncFParam;
import frontend.datastruct.ast.AstVarDef;
import frontend.datastruct.ast.Token;
import ir.datastruct.operand.Operand;
import ir.datastruct.operand.Reg;
import ir.datastruct.operand.Var;

public class SymVar extends Symbol {
    public Var irVar;
    public boolean isGlobal = false;
    public final boolean isArray;
    public final boolean isReference;

    // Compiler generated.
    public SymVar(String name) {
        super(SymId.Int, new Token(Token.TokenId.IDENFR, name, 0));
        isArray = false;
        isReference = false;
        isGlobal = true;
    }

    public SymVar(AstVarDef varDef) {
        super(
                varDef.constExp == null ? (
                                varDef.type.tokenId == Token.TokenId.INTTK ? SymId.Int : SymId.Char
                        ) : (
                                varDef.type.tokenId == Token.TokenId.INTTK ? SymId.IntArray : SymId.CharArray
                        ),
                varDef.ident
        );
        isReference = false; // We have no ref-operator in our grammar.
        isArray = varDef.constExp != null;
    }

    public SymVar(AstFuncFParam param) {
        super(
                !param.isArray ? (
                                param.type.tokenId == Token.TokenId.INTTK ? SymId.Int : SymId.Char
                        ) : (
                                param.type.tokenId == Token.TokenId.INTTK ? SymId.IntArray : SymId.CharArray
                        ),
        param.ident);
        isReference = param.isArray;
        isArray = false;
    }
}
