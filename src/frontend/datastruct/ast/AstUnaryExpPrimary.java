package frontend.datastruct.ast;

public class AstUnaryExpPrimary extends AstUnaryExp {
    public AstPrimaryExp primaryExp;

    @Override
    public String toString() {
        return primaryExp.toString();
    }

    public void setPrimaryExp(AstPrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }
}
