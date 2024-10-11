package datastruct.ast;

public class AstLVal extends AstNode {
    public AstLVal() {
        super(AstNodeId.LVal);
    }

    public Token ident;
    public AstExp exp = null;

    public void setIdent(Token ident) {
        this.ident = ident;
    }

    public void setExp(AstExp exp) {
        this.exp = exp;
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
