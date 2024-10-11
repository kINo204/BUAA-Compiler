package datastruct.ast;

public class AstUnaryExpFuncCall extends AstUnaryExp {
    public Token funcIdent;
    public AstFuncRParams funcRParams = null;

    public void setFuncIdent(Token funcIdent) {
        this.funcIdent = funcIdent;
    }

    public void setFuncRParams(AstFuncRParams funcRParams) {
        this.funcRParams = funcRParams;
    }
}
