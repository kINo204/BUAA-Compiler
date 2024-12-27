package frontend.datastruct.ast;

import java.util.ArrayList;
import java.util.StringJoiner;

public class AstConstDecl extends AstNode {
    public AstConstDecl() {
        super(AstNodeId.ConstDecl);
    }

    public Token type;
    public final ArrayList<AstConstDef> constDefs = new ArrayList<>();

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        for (AstConstDef constDef : constDefs) {
            sj.add(constDef.toString());
        }
        return String.format("const %s %s;", type, sj);
    }

    public void setType(Token type) {
        this.type = type;
    }

    public void addConstDef(AstConstDef constDef) {
        constDefs.add(constDef);
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
