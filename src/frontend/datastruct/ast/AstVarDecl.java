package frontend.datastruct.ast;

import java.util.ArrayList;
import java.util.StringJoiner;

public class AstVarDecl extends AstNode {
    public AstVarDecl() {
        super(AstNodeId.VarDecl);
    }

    public Token type;
    public final ArrayList<AstVarDef> varDefs = new ArrayList<>();

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        for (AstVarDef varDef : varDefs) {
            sj.add(varDef.toString());
        }
        return String.format("%s %s;", type, sj);
    }

    public void setType(Token type) {
        this.type = type;
    }

    public void addVarDef(AstVarDef varDef) {
        varDefs.add(varDef);
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
