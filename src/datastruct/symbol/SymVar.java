package datastruct.symbol;

import datastruct.ast.AstFuncFParam;
import datastruct.ast.AstVarDef;
import datastruct.ast.Token;

public class SymVar extends Symbol {
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
