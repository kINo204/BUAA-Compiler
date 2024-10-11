package datastruct.symbol;

import datastruct.ast.AstFuncDef;

public class SymFunc extends Symbol {
    public SymFunc(AstFuncDef n) {
        super(
            switch (n.funcType.type.getTokenId()) {
                case CHARTK -> SymId.CharFunc;
                case INTTK -> SymId.IntFunc;
                case VOIDTK -> SymId.VoidFunc;
                default -> null;
            }
        , n.ident);
    }
}
