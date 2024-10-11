package datastruct.ast;

public class AstUnaryExpPrimary extends AstUnaryExp {
    public AstPrimaryExp primaryExp;

    public void setPrimaryExp(AstPrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }
}
