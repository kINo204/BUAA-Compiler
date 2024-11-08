package frontend.datastruct.ast;

public class AstNumber extends AstNode {
    public AstNumber() {
        super(AstNodeId.Number);
    }

    public Token intConst;

    @Override
    public String toString() {
        return intConst.toString();
    }

    public void setIntConst(Token intConst) {
        this.intConst = intConst;
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
