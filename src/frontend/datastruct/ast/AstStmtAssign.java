package frontend.datastruct.ast;

public class AstStmtAssign extends AstStmt {
    public AstLVal lVal;
    public AstExp exp;

    @Override
    public String toString() {
        return lVal + " = " + exp + ";";
    }

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
