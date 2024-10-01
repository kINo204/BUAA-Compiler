package datastruct.ast;

import datastruct.Token;

import java.util.ArrayList;

public class AstVarDecl extends AstNode {
    public AstVarDecl() {
        super(AstNodeId.VarDecl);
    }

    private Token type;
    private final ArrayList<AstVarDef> varDefs = new ArrayList<>();

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
