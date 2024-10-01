package datastruct.ast;

import datastruct.Token;

public class AstConstDef extends AstNode{
    public AstConstDef() {
        super(AstNodeId.ConstDef);
    }

    private Token ident;
    private AstConstExp constExp = null;
    private AstConstInitVal constInitVal;

    public void setIdent(Token ident) {
        this.ident = ident;
    }

    public void setConstExp(AstConstExp constExp) {
        this.constExp = constExp;
    }

    public void setConstInitVal(AstConstInitVal constInitVal) {
        this.constInitVal = constInitVal;
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
