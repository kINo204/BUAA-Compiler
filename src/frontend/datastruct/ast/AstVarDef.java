package frontend.datastruct.ast;

public class AstVarDef extends AstNode {
    public AstVarDef() {
        super(AstNodeId.VarDef);
    }

    public Token type;
    public Token ident;
    public AstConstExp constExp = null;
    public AstInitVal initVal = null;

    @Override
    public String toString() {
        String s = ident.toString();
        if (constExp != null) {
            s += String.format("[%s]", constExp);
        }
        if (initVal != null) {
            s += " = " + initVal;
        }
        return s;
    }

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
