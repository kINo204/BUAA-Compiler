package datastruct.ast;

import datastruct.Token;

public class AstUnaryExpFuncCall extends AstUnaryExp {
    private Token funcIdent;
    private AstFuncRParams funcRParams = null;

    public void setFuncIdent(Token funcIdent) {
        this.funcIdent = funcIdent;
    }

    public void setFuncRParams(AstFuncRParams funcRParams) {
        this.funcRParams = funcRParams;
    }
}
