package frontend.datastruct.ast;

import java.util.ArrayList;
import java.util.StringJoiner;

public class AstInitVal extends AstNode {
    public AstInitVal() {
        super(AstNodeId.InitVal);
    }

    public AstExp exp = null;
    public final ArrayList<AstExp> exps = new ArrayList<>();
    public Token stringConst = null;


    @Override
    public String toString() {
        if (exp != null) {
            return exps.toString();
        } else if (stringConst != null) {
            return stringConst.toString(); // Including the "" symbols.
        } else {
            StringJoiner sj = new StringJoiner(", ");
            for (AstExp e : exps) {
                sj.add(e.toString());
            }
            return sj.toString();
        }
    }

    public void setExp(AstExp exp) {
        this.exp = exp;
    }

    public void addExp(AstExp exp) {
        exps.add(exp);
    }

    public void setStringConst(Token stringConst) {
        this.stringConst = stringConst;
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
