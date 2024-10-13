package datastruct.ast;

public class AstConstExp extends AstNode {
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
