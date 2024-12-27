package frontend.datastruct.ast;

import java.util.ArrayList;
import java.util.StringJoiner;

public class AstLAndExp extends AstNode {
    public AstLAndExp() {
        super(AstNodeId.LAndExp);
    }

    public final ArrayList<AstEqExp> eqExps = new ArrayList<>();

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(" && ");
        for (AstEqExp e : eqExps) {
            sj.add(e.toString());
        }
        return sj.toString();
    }

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
