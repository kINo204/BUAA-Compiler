package datastruct.ast;

import datastruct.Token;

public class AstFuncType extends AstNode {
    public AstFuncType() {
        super(AstNodeId.FuncType);
    }

    private Token type;

    public void setType(Token type) {
        this.type = type;
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
