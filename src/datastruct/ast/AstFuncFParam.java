package datastruct.ast;

public class AstFuncFParam extends AstNode{
    public AstFuncFParam() {
        super(AstNodeId.FuncFParam);
    }

    public Token type;
    public Token ident;
    public boolean isArray;

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
