package frontend.datastruct.ast;

public class AstStmtGetint extends AstStmt {
    public AstLVal lVal;

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
