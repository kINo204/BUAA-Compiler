package datastruct.ast;

import java.util.ArrayList;

public class AstEqExp extends AstNode {
    public AstEqExp() {
        super(AstNodeId.EqExp);
    }

    public final ArrayList<AstRelExp> relExps = new ArrayList<>();
    public ArrayList<Token.TokenId> operators = new ArrayList<>();

    public void addRelExp(AstRelExp relExp) {
        relExps.add(relExp);
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
