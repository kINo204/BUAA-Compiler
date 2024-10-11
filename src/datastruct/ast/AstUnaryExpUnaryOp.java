package datastruct.ast;

public class AstUnaryExpUnaryOp extends AstUnaryExp {
    public AstUnaryOp unaryOp;
    public AstUnaryExp unaryExp;

    public void setUnaryOp(AstUnaryOp unaryOp) {
        this.unaryOp = unaryOp;
    }

    public void setUnaryExp(AstUnaryExp unaryExp) {
        this.unaryExp = unaryExp;
    }
}
