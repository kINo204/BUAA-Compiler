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
        for (AstMulExp mulExp : mulExps) {
            str.append(mulExp.toString());
            str.append("+");
        }
        str.deleteCharAt(str.length() - 1);
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
