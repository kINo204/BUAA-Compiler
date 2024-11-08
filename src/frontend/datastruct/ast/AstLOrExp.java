package frontend.datastruct.ast;

import java.util.ArrayList;

public class AstLOrExp extends AstNode {
    public AstLOrExp() {
        super(AstNodeId.LOrExp);
    }

    public final ArrayList<AstLAndExp> lAndExps = new ArrayList<>();

    public void addLAndExp(AstLAndExp lAndExp) {
        lAndExps.add(lAndExp);
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
