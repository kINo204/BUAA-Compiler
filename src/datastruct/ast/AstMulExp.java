package datastruct.ast;

import datastruct.symbol.Symbol;

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
        StringBuilder stringBuilder = new StringBuilder();
        for (AstUnaryExp unaryExp : unaryExps) {
            stringBuilder.append(unaryExp);
            stringBuilder.append("*");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
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
