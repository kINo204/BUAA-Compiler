package frontend.datastruct.ast;

import frontend.datastruct.symbol.Symbol;

import java.util.ArrayList;

public class AstAddExp extends AstNode {
    public Symbol.SymId type = null;

    public AstAddExp() {
        super(AstNodeId.AddExp);
    }

    public final ArrayList<AstMulExp> mulExps = new ArrayList<>();
    public final ArrayList<Token.TokenId> operators = new ArrayList<>();

    public void addMulExp(AstMulExp mulExp) {
        mulExps.add(mulExp);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(mulExps.get(0).toString());
        for (int i = 1; i < mulExps.size(); i++) {
            str.append(switch (operators.get(i - 1)) {
                case PLUS -> " + ";
                case MINU -> " - ";
                default -> {
                    assert false;
                    yield "";
                }
            });
            str.append(mulExps.get(i).toString());
        }
        return str.toString();
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
