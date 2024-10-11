package datastruct.ast;

public class AstFuncDef extends AstNode{
    public AstFuncDef() {
        super(AstNodeId.FuncDef);
    }

    public AstFuncType funcType;
    public Token ident;
    public AstFuncFParams funcFParams = null;
    public AstBlock block;

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
