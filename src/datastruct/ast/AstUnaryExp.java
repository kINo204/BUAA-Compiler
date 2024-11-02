package datastruct.ast;

import datastruct.symbol.Symbol;

public class AstUnaryExp extends AstNode {
    public Symbol.SymId type = null;

    public AstUnaryExp() {
        super(AstNodeId.UnaryExp);
    }

    @Override
    public String buildTreeBrack() {
        return null;
    }

    @Override
    public String buildTreeConsole() {
        return null;
    }
}
