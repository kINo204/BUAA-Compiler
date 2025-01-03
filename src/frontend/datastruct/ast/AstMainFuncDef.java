package frontend.datastruct.ast;

public class AstMainFuncDef extends AstNode{
    public AstMainFuncDef() {
        super(AstNodeId.MainFuncDef);
    }

    public AstBlock block;

    @Override
    public String toString() {
        return "int main () " + block.toString();
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
