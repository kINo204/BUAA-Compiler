package datastruct.ast;

import datastruct.Token;

public class AstVarDef extends AstNode {
    public AstVarDef() {
        super(AstNodeId.VarDef);
    }

    private Token ident;
    private AstConstExp constExp = null;
    private AstInitVal initVal = null;

    public void setIdent(Token ident) {
        this.ident = ident;
    }

    public void setConstExp(AstConstExp constExp) {
        this.constExp = constExp;
    }

    public void setInitVal(AstInitVal initVal) {
        this.initVal = initVal;
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
