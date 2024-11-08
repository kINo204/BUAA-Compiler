package frontend.datastruct.ast;

public class AstStmtIf extends AstStmt {
    public AstCond cond;
    public AstStmt ifStmt;
    public AstStmt elseStmt = null;

    public void setCond(AstCond cond) {
        this.cond = cond;
    }

    public void setIfStmt(AstStmt ifStmt) {
        this.ifStmt = ifStmt;
    }

    public void setElseStmt(AstStmt elseStmt) {
        this.elseStmt = elseStmt;
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
