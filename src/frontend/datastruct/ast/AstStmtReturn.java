package frontend.datastruct.ast;

public class AstStmtReturn extends AstStmt {

    public Token returnTk;
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
