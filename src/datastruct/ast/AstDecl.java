package datastruct.ast;

public class AstDecl extends AstNode{
    public AstDecl() {
        super(AstNodeId.Decl);
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
