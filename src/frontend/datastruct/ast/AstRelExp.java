package frontend.datastruct.ast;

import java.util.ArrayList;

public class AstRelExp extends AstNode {
    public AstRelExp() {
        super(AstNodeId.RelExp);
    }

    public ArrayList<AstAddExp> addExps = new ArrayList<>();
    public final ArrayList<Token.TokenId> operators = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(addExps.get(0).toString());
        for (int i = 1; i < addExps.size(); i++) {
            str.append(switch (operators.get(i - 1)) {
                case GRE -> " > ";
                case LSS -> " < ";
                case GEQ -> " >= ";
                case LEQ -> " <= ";
                default -> {
                    assert false;
                    yield "";
                }
            });
            str.append(addExps.get(i).toString());
        }
        return str.toString();
    }

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
