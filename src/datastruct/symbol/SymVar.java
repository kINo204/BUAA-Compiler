package datastruct.symbol;

import datastruct.ast.AstConstDef;
import datastruct.ast.AstFuncFParam;
import datastruct.ast.AstVarDef;
import datastruct.ast.Token;

public class SymVar extends Symbol {
    public SymVar(Token.TokenId typePrefix, AstVarDef n) {
        super(
                 switch (typePrefix) {
                        case INTTK -> n.constExp == null ? SymId.Int : SymId.IntArray;
                        case CHARTK -> n.constExp == null ? SymId.Char : SymId.CharArray;
                        default -> null; // Error
                }
        , n.ident);
    }

    public SymVar(Token.TokenId typePrefix, AstConstDef n) {
        super(
                switch (typePrefix) {
                    case INTTK -> n.constExp == null ? SymId.ConstInt: SymId.ConstIntArray;
                    case CHARTK -> n.constExp == null ? SymId.ConstChar : SymId.ConstCharArray;
                    default -> null; // Error
                }
        , n.ident);
    }

    public SymVar(AstFuncFParam n) {
        super(
                n.type.tokenId == Token.TokenId.INTTK ? (!n.isArray ? SymId.Int : SymId.IntArray) :
                n.type.tokenId == Token.TokenId.CHARTK ? (!n.isArray ? SymId.Char : SymId.CharArray) :
                null, n.ident);
    }
}
