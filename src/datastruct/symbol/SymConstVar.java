package datastruct.symbol;

import datastruct.ast.AstConstDef;
import datastruct.ast.Token;

public class SymConstVar extends Symbol {
    public final boolean isArray;

    public SymConstVar(AstConstDef constDef) {
        super(
                constDef.constExp == null ? (
                                constDef.type.tokenId == Token.TokenId.INTTK ? SymId.ConstInt : SymId.ConstChar
                        ) : (
                                constDef.type.tokenId == Token.TokenId.INTTK ? SymId.ConstIntArray : SymId.ConstCharArray
                        ),
                constDef.ident
        );
        isArray = constDef.constExp != null;
    }
}
