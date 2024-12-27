package frontend.datastruct.ast;

import frontend.datastruct.symbol.Symbol;

import java.util.ArrayList;

public class AstMulExp extends AstNode{
    public Symbol.SymId type = null;

    public AstMulExp() {
        super(AstNodeId.MulExp);
    }

    public final ArrayList<AstUnaryExp> unaryExps = new ArrayList<>();
    public final ArrayList<Token.TokenId> operators = new ArrayList<>();

    public void addUnaryExp(AstUnaryExp unaryExp) {
        unaryExps.add(unaryExp);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(unaryExps.get(0).toString());
        for (int i = 1; i < unaryExps.size(); i++) {
            str.append(switch (operators.get(i - 1)) {
                case MULT -> " * ";
                case DIV  -> " / ";
                default -> {
                    assert false;
                    yield "";
                }
            });
            str.append(unaryExps.get(i).toString());
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
