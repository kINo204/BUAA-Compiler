package frontend.datastruct.ast;

import java.util.ArrayList;

public class AstConstDecl extends AstNode {
    public AstConstDecl() {
        super(AstNodeId.ConstDecl);
    }

    public Token type;
    public final ArrayList<AstConstDef> constDefs = new ArrayList<>();

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
