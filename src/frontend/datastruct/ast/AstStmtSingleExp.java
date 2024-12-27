package frontend.datastruct.ast;

public class AstStmtSingleExp extends AstStmt {
    public AstExp exp = null;

    @Override
    public String toString() {
        return exp + ";";
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
