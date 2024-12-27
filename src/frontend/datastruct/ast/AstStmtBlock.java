package frontend.datastruct.ast;

public class AstStmtBlock extends AstStmt {
    public AstBlock block;

    @Override
    public String toString() {
        return block.toString();
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
