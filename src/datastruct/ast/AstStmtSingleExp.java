package datastruct.ast;

public class AstStmtSingleExp extends AstStmt {
    public AstExp exp = null;

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
