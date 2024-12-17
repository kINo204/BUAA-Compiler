package frontend.datastruct.ast;

import frontend.datastruct.symbol.Symbol;

public class AstConstExp extends AstNode {
    public Symbol.SymId type = null;

    public AstConstExp() {
        super(AstNodeId.ConstExp);
    }

    public AstAddExp addExp;

    @Override
    public String toString() {
        return addExp.toString();
    }

    public void setAddExp(AstAddExp addExp) {
        this.addExp = addExp;
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
