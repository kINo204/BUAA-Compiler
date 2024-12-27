package frontend.datastruct.ast;

public class AstBlockItem extends AstNode {
    public AstBlockItem() {
        super(AstNodeId.BlockItem);
    }

    public AstNode content;

    @Override
    public String toString() {
        return content.toString();
    }

    public void setContent(AstNode content) {
        this.content = content;
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
