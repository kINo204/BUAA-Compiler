package frontend.datastruct.ast;

public class AstStmtReturn extends AstStmt {

    public Token returnTk;
    public AstExp exp = null;

    @Override
    public String toString() {
        if (exp == null) {
            return "return;";
        } else {
            return "return " + exp + ";";
        }
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
