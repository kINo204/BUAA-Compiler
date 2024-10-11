package datastruct.ast;

public class AstStmtBlock extends AstStmt {
    public AstBlock block;

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
