package frontend.datastruct.ast;

public class AstStmtFor extends AstStmt {
    public AstForStmt firstForStmt = null;
    public AstCond cond = null;
    public AstForStmt thirdForStmt = null;
    public AstStmt stmt;

    @Override
    public String toString() {
        return String.format("for (%s; %s; %s) %s",
                firstForStmt != null ? firstForStmt : "",
                cond != null ? cond : "",
                thirdForStmt != null ? thirdForStmt : "",
                stmt);
    }

    public void setFirstForStmt(AstForStmt firstForStmt) {
        this.firstForStmt = firstForStmt;
    }

    public void setCond(AstCond cond) {
        this.cond = cond;
    }

    public void setThirdForStmt(AstForStmt thirdForStmt) {
        this.thirdForStmt = thirdForStmt;
    }

    public void setStmt(AstStmt stmt) {
        this.stmt = stmt;
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
