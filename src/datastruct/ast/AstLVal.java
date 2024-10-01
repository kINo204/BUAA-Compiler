package datastruct.ast;

import datastruct.Token;

public class AstLVal extends AstNode {
    public AstLVal() {
        super(AstNodeId.LVal);
    }

    private Token ident;
    private AstExp exp = null;

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
