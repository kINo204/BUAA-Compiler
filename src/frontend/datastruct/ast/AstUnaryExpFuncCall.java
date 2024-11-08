package frontend.datastruct.ast;

public class AstUnaryExpFuncCall extends AstUnaryExp {
    public Token funcIdent;
    public AstFuncRParams funcRParams = null;

    @Override
    public String toString() {
        return funcIdent.literal + "(" + funcRParams.toString() + ")";
    }

    public void setFuncIdent(Token funcIdent) {
        this.funcIdent = funcIdent;
    }

    public void setFuncRParams(AstFuncRParams funcRParams) {
        this.funcRParams = funcRParams;
    }
}
