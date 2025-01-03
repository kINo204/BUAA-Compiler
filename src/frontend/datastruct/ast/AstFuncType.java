package frontend.datastruct.ast;

public class AstFuncType extends AstNode {
    public AstFuncType() {
        super(AstNodeId.FuncType);
    }

    public Token type;

    @Override
    public String toString() {
        return type.toString();
    }

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
