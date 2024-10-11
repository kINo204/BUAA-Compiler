package datastruct.ast;

public class AstCond extends AstNode {
    public AstCond() {
        super(AstNodeId.Cond);
    }

    public AstLOrExp lOrExp;

    public void setlOrExp(AstLOrExp lOrExp) {
        this.lOrExp = lOrExp;
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
