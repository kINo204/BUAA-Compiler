package datastruct.symbol;

import datastruct.ast.AstFuncDef;
import datastruct.ast.AstFuncFParam;
import ir.datastruct.operand.FuncRef;

import java.util.ArrayList;

import static datastruct.ast.Token.TokenId.CHARTK;
import static datastruct.ast.Token.TokenId.INTTK;

public class SymFunc extends Symbol {
    public final ArrayList<SymId> paramTypes = new ArrayList<>();
    public final ArrayList<Symbol> params = new ArrayList<>();
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
                        paramTypes.add(SymId.IntArray);
                    } else {
                        paramTypes.add(SymId.Int);
                    }
                } else if (param.type.tokenId == CHARTK) {
                    if (param.isArray) {
                        paramTypes.add(SymId.CharArray);
                    } else {
                        paramTypes.add(SymId.Char);
                    }
                } else assert false; // invalid token
        }
    }

    public void addParamSym(Symbol symParam) {
        this.params.add(symParam);
    }
}
