package frontend.datastruct.ast;

import java.util.ArrayList;

public class AstBlock extends AstNode{
    public AstBlock() {
        super(AstNodeId.Block);
    }

    public final ArrayList<AstBlockItem> blockItems = new ArrayList<>();
    public Token braceEnd;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("{\n");

        for (AstBlockItem blockItem : blockItems) {
            s.append("\t").append(blockItem).append("\n");
        }

        s.append("}");
        return s.toString();
    }

    public void addBlockItem(AstBlockItem blockItem) {
        blockItems.add(blockItem);
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
