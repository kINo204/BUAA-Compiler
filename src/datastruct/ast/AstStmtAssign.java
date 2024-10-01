package datastruct.ast;

public class AstStmtAssign extends AstStmt {
    private AstLVal lVal;
    private AstExp exp;

    public void setExp(AstExp exp) {
        this.exp = exp;
    }

    public void setlVal(AstLVal lVal) {
        this.lVal = lVal;
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
