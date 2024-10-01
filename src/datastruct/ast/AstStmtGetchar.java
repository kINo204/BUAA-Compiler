package datastruct.ast;

public class AstStmtGetchar extends AstStmt {
    private AstLVal lVal;

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
