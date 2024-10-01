package datastruct.ast;

public class AstPrimaryExp extends AstNode {
    public AstPrimaryExp() {
        super(AstNodeId.PrimaryExp);
    }

    // Choose one between the four:
    private AstExp bracedExp = null;
    private AstLVal lVal = null;
    private AstNumber number = null;
    private AstCharacter character = null;

    public void setBracedExp(AstExp bracedExp) {
        this.bracedExp = bracedExp;
    }

    public void setlVal(AstLVal lVal) {
        this.lVal = lVal;
    }

    public void setNumber(AstNumber number) {
        this.number = number;
    }

    public void setCharacter(AstCharacter character) {
        this.character = character;
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
