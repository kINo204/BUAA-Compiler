package datastruct.ast;

import datastruct.Token;

public class AstFuncDef extends AstNode{
    public AstFuncDef() {
        super(AstNodeId.FuncDef);
    }

    private AstFuncType funcType;
    private Token ident;
    private AstFuncFParams funcFParams = null;
    private AstBlock block;

    public void setFuncType(AstFuncType funcType) {
        this.funcType = funcType;
    }

    public void setIdent(Token ident) {
        this.ident = ident;
    }

    public void setFuncFParams(AstFuncFParams funcFParams) {
        this.funcFParams = funcFParams;
    }

    public void setBlock(AstBlock block) {
        this.block = block;
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
