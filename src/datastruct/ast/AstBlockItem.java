package datastruct.ast;

public class AstBlockItem extends AstNode {
    public AstBlockItem() {
        super(AstNodeId.BlockItem);
    }

    private AstNode content;

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
