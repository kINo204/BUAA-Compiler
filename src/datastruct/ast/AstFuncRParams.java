package datastruct.ast;

import java.util.ArrayList;

public class AstFuncRParams extends AstNode {
    public AstFuncRParams() {
        super(AstNodeId.FuncRParams);
    }

    public final ArrayList<AstExp> exps = new ArrayList<>();

    public void addExp(AstExp exp) {
        exps.add(exp);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (AstExp exp : exps) {
            stringBuilder.append(exp).append(",");
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
}