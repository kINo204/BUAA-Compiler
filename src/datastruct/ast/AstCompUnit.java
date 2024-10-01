package datastruct.ast;

import java.util.ArrayList;

public class AstCompUnit extends AstNode{
    public AstCompUnit() {
        super(AstNodeId.CompUnit);
    }

    private final ArrayList<AstDecl> decls = new ArrayList<>();
    private final ArrayList<AstFuncDef> funcDefs = new ArrayList<>();
    private AstMainFuncDef mainFuncDef;

    public void addDecl(AstDecl decl) {
        decls.add(decl);
    }

    public void addFuncDef(AstFuncDef funcDef) {
        funcDefs.add(funcDef);
    }

    public void setMainFuncDef(AstMainFuncDef mainFuncDef) {
        this.mainFuncDef = mainFuncDef;
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
