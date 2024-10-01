package datastruct.ast;

public class AstConstExp extends AstNode {
    public AstConstExp() {
        super(AstNodeId.ConstExp);
    }

    private AstAddExp astAddExp;

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
