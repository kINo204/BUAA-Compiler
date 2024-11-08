package frontend.datastruct.ast;

import java.util.ArrayList;

public class AstStmtPrintf extends AstStmt {
    public Token printfTk;
    public Token stringConst;
    public final ArrayList<AstExp> exps = new ArrayList<>();

    public void setStringConst(Token stringConst) {
        this.stringConst = stringConst;
    }

    public void addExp(AstExp exp) {
        exps.add(exp);
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
