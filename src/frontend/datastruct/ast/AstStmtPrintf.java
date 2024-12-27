package frontend.datastruct.ast;

import java.util.ArrayList;
import java.util.StringJoiner;

public class AstStmtPrintf extends AstStmt {
    public Token printfTk;
    public Token stringConst;
    public final ArrayList<AstExp> exps = new ArrayList<>();

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        sj.add(stringConst.toString());
        for (AstExp exp : exps) {
            sj.add(exp.toString());
        }
        return String.format("printf(%s);", sj);
    }

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
