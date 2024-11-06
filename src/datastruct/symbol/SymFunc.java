package datastruct.symbol;

import datastruct.ast.AstFuncDef;
import datastruct.ast.AstFuncFParam;
import datastruct.ast.Token;
import ir.datastruct.operand.FuncRef;

import java.util.ArrayList;

import static datastruct.ast.Token.TokenId.CHARTK;
import static datastruct.ast.Token.TokenId.INTTK;

public class SymFunc extends Symbol {
    public final ArrayList<SymId> types = new ArrayList<>();
    public FuncRef funcRef;

    public SymFunc(AstFuncDef n) {
        super(
            switch (n.funcType.type.getTokenId()) {
                case CHARTK -> SymId.CharFunc;
                case INTTK -> SymId.IntFunc;
                case VOIDTK -> SymId.VoidFunc;
                default -> null;
            }
        , n.ident);
        if (n.funcFParams != null) { // No is-const considered!
            for (AstFuncFParam param : n.funcFParams.funcFParams)
                if (param.type.tokenId == INTTK) {
                    if (param.isArray) {
                        types.add(SymId.IntArray);
                    } else {
                        types.add(SymId.Int);
                    }
                } else if (param.type.tokenId == CHARTK) {
                    if (param.isArray) {
                        types.add(SymId.CharArray);
                    } else {
                        types.add(SymId.Char);
                    }
                } else assert false; // invalid token
        }
    }
}
