package datastruct.ast;

import datastruct.Token;

public class AstUnaryOp extends AstNode {
    public AstUnaryOp() {
        super(AstNodeId.UnaryOp);
    }

    private Token op;

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
