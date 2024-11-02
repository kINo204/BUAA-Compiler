package datastruct.ast;

import datastruct.symbol.Symbol;

public class AstPrimaryExp extends AstNode {
    public Symbol.SymId type = null;

    public AstPrimaryExp() {
        super(AstNodeId.PrimaryExp);
    }

    // Choose one between the four:
    public AstExp bracedExp = null;
    public AstLVal lVal = null;
    public AstNumber number = null;
    public AstCharacter character = null;

    @Override
    public String toString() {
        if (bracedExp != null) {
            return "(" + bracedExp + ")";
        } else if (lVal != null) {
            return lVal.toString();
        } else if (number != null) {
            return number.toString();
        } else {
            return character.toString();
        }
    }

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
