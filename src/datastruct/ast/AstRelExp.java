package datastruct.ast;

import java.util.ArrayList;

public class AstRelExp extends AstNode {
    public AstRelExp() {
        super(AstNodeId.RelExp);
    }

    public final ArrayList<AstAddExp> addExps = new ArrayList<>();

    public void addAddExp(AstAddExp addExp) {
        addExps.add(addExp);
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
