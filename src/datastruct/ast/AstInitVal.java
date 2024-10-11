package datastruct.ast;

import java.util.ArrayList;

public class AstInitVal extends AstNode {
    public AstInitVal() {
        super(AstNodeId.InitVal);
    }

    public AstExp exp = null;
    public final ArrayList<AstExp> exps = new ArrayList<>();
    public Token stringConst = null;

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
