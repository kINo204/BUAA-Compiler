package datastruct.symbol;

import datastruct.ast.AstFuncFParam;
import datastruct.ast.AstVarDef;
import datastruct.ast.Token;
import ir.datastruct.operand.Operand;
import ir.datastruct.operand.Reg;
import ir.datastruct.operand.Var;

public class SymVar extends Symbol {
    public Var irVar;
    public final boolean isArray;

    public SymVar(AstVarDef varDef) {
        super(
                varDef.constExp == null ? (
                                varDef.type.tokenId == Token.TokenId.INTTK ? SymId.Int : SymId.Char
                        ) : (
                                varDef.type.tokenId == Token.TokenId.INTTK ? SymId.IntArray : SymId.CharArray
                        ),
                varDef.ident
        );
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
        isArray = param.isArray;
    }
}
