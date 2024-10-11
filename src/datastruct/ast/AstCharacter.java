package datastruct.ast;

public class AstCharacter extends AstNode {
    public AstCharacter() {
        super(AstNodeId.Character);
    }

    public Token charConst;

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
