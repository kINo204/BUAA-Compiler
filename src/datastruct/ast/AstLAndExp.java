package datastruct.ast;

import java.util.ArrayList;

public class AstLAndExp extends AstNode {
    public AstLAndExp() {
        super(AstNodeId.LAndExp);
    }

    private final ArrayList<AstEqExp> eqExps = new ArrayList<>();

    public void addEqExp(AstEqExp eqExp) {
        eqExps.add(eqExp);
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
