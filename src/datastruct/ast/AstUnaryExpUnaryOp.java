package datastruct.ast;

public class AstUnaryExpUnaryOp extends AstUnaryExp {
    private AstUnaryOp unaryOp;
    private AstUnaryExp unaryExp;

    public void setUnaryOp(AstUnaryOp unaryOp) {
        this.unaryOp = unaryOp;
    }

    public void setUnaryExp(AstUnaryExp unaryExp) {
        this.unaryExp = unaryExp;
    }
}
