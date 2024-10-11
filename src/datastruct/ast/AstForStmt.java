package datastruct.ast;

public class AstForStmt extends AstNode {
    public AstForStmt() {
        super(AstNodeId.ForStmt);
    }

    public AstLVal lVal;
    public AstExp exp;

    public void setlVal(AstLVal lVal) {
        this.lVal = lVal;
    }

    public void setExp(AstExp exp) {
        this.exp = exp;
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
