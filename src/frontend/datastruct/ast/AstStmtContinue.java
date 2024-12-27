package frontend.datastruct.ast;

public class AstStmtContinue extends AstStmt {
    public Token token;

    @Override
    public String toString() {
        return token.toString() + ";";
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
