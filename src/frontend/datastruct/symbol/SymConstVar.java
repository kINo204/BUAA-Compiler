package frontend.datastruct.symbol;

import frontend.datastruct.ast.AstConstDef;
import frontend.datastruct.ast.Token;
import ir.datastruct.operand.Operand;
import ir.datastruct.operand.Var;

import java.util.ArrayList;

public class SymConstVar extends Symbol {
    public Var irVar;
    public final boolean isArray;
    public final ArrayList<Object> values = new ArrayList<>();

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
