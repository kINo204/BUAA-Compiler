package datastruct.ast;

public class AstStmtFor extends AstStmt {
    private AstForStmt firstForStmt = null;
    private AstCond cond = null;
    private AstForStmt thirdForStmt = null;
    private AstStmt stmt;

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
