package frontend.datastruct.ast;

public class AstUnaryOp extends AstNode {
    public AstUnaryOp() {
        super(AstNodeId.UnaryOp);
    }

    public Token op;

    public void setOp(Token op) {
        this.op = op;
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
