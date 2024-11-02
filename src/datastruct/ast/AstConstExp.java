package datastruct.ast;

import datastruct.symbol.Symbol;

public class AstConstExp extends AstNode {
    public Symbol.SymId type = null;

    public AstConstExp() {
        super(AstNodeId.ConstExp);
    }

    public AstAddExp astAddExp;

    @Override
    public String toString() {
        return astAddExp.toString();
    }

    public void setAstAddExp(AstAddExp astAddExp) {
        this.astAddExp = astAddExp;
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
