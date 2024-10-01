package datastruct.ast;

import datastruct.Token;

public class AstCharacter extends AstNode {
    public AstCharacter() {
        super(AstNodeId.Character);
    }

    private Token charConst;

    public void setCharConst(Token charConst) {
        this.charConst = charConst;
    }

    @Override
    public String buildTreeBrack() {
        return null;
    }

    @Override
    public  String buildTreeConsole() {
        return null;
    }
}
