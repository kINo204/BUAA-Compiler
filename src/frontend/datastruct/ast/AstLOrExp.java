package frontend.datastruct.ast;

import java.util.ArrayList;
import java.util.StringJoiner;

public class AstLOrExp extends AstNode {
    public AstLOrExp() {
        super(AstNodeId.LOrExp);
    }

    public final ArrayList<AstLAndExp> lAndExps = new ArrayList<>();

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(" || ");
        for (AstLAndExp e : lAndExps) {
            sj.add(e.toString());
        }
        return sj.toString();
    }

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
