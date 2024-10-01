package datastruct.ast;

import java.util.ArrayList;

public class AstAddExp extends AstNode {
    public AstAddExp() {
        super(AstNodeId.AddExp);
    }

    private final ArrayList<AstMulExp> mulExps = new ArrayList<>();

    public void addMulExp(AstMulExp mulExp) {
        mulExps.add(mulExp);
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
