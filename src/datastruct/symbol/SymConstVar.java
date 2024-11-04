package datastruct.symbol;

import datastruct.ast.AstConstDef;
import datastruct.ast.Token;
import ir.datastruct.operand.Operand;
import ir.datastruct.operand.Var;

public class SymConstVar extends Symbol {
    public Var irVar;
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
