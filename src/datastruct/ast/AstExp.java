package datastruct.ast;

public class AstExp extends AstNode {
    public AstExp() {
        super(AstNodeId.Exp);
    }

    public AstAddExp astAddExp;

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
