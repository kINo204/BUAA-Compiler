package frontend.datastruct.ast;

import java.util.ArrayList;

public class AstRelExp extends AstNode {
    public AstRelExp() {
        super(AstNodeId.RelExp);
    }

    public ArrayList<AstAddExp> addExps = new ArrayList<>();
    public final ArrayList<Token.TokenId> operators = new ArrayList<>();

    public void addAddExp(AstAddExp addExp) {
        addExps.add(addExp);
    }

    @Override
    public String buildTreeBrack() {
        return null;
    }

    @Override
    public String buildTreeConsole() {
        return null;
    }

    public void addOperator(Token read) {
        operators.add(read.tokenId);
    }
}
