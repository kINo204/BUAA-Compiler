package datastruct.ast;

import datastruct.Token;

public class AstFuncFParam extends AstNode{
    public AstFuncFParam() {
        super(AstNodeId.FuncFParam);
    }

    private Token type;
    private Token ident;
    private boolean isArray;

    public void setType(Token type) {
        this.type = type;
    }

    public void setIdent(Token ident) {
        this.ident = ident;
    }

    public void setArray(boolean array) {
        isArray = array;
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
