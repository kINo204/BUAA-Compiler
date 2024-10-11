package datastruct.ast;

import java.util.ArrayList;

public class AstEqExp extends AstNode {
    public AstEqExp() {
        super(AstNodeId.EqExp);
    }

    public final ArrayList<AstRelExp> relExps = new ArrayList<>();

    public void addRelExp(AstRelExp relExp) {
        relExps.add(relExp);
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
