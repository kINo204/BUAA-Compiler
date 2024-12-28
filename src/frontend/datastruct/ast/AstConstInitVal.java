package frontend.datastruct.ast;

import java.util.ArrayList;
import java.util.StringJoiner;

public class AstConstInitVal extends AstNode{
    public AstConstInitVal() {
        super(AstNodeId.ConstInitVal);
    }

    public AstConstExp constExp = null;
    public final ArrayList<AstConstExp> constExps = new ArrayList<>();
    public Token stringConst = null;

    @Override
    public String toString() {
        if (constExp != null) {
            return constExp.toString();
        } else if (stringConst != null) {
            return stringConst.toString(); // Including the "" symbols.
        } else {
            StringJoiner sj = new StringJoiner(", ");
            for (AstConstExp e : constExps) {
                sj.add(e.toString());
            }
            return "{" + sj + "}";
        }
    }

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
