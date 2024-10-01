package datastruct.ast;

public class AstUnaryExpPrimary extends AstUnaryExp {
    private AstPrimaryExp primaryExp;

    public void setPrimaryExp(AstPrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }
}
