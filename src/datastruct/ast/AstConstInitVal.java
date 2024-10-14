package datastruct.ast;

import java.util.ArrayList;

public class AstConstInitVal extends AstNode{
    public AstConstInitVal() {
        super(AstNodeId.ConstInitVal);
    }

    public AstConstExp constExp = null;
    public final ArrayList<AstConstExp> constExps = new ArrayList<>();
    public Token stringConst;

    public void setConstExp(AstConstExp constExp) {
        this.constExp = constExp;
    }

    public void addConstExp(AstConstExp constExp) {
        constExps.add(constExp);
    }

    public void setStringConst(Token stringConst) {
        this.stringConst = stringConst;
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