package frontend.datastruct.ast;

import java.util.ArrayList;

public class AstEqExp extends AstNode {
    public AstEqExp() {
        super(AstNodeId.EqExp);
    }

    public final ArrayList<AstRelExp> relExps = new ArrayList<>();
    public ArrayList<Token.TokenId> operators = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(relExps.get(0).toString());
        for (int i = 1; i < relExps.size(); i++) {
            str.append(switch (operators.get(i - 1)) {
                case EQL -> " == ";
                case NEQ -> " != ";
                default -> {
                    assert false;
                    yield "";
                }
            });
            str.append(relExps.get(i).toString());
        }
        return str.toString();
    }

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
