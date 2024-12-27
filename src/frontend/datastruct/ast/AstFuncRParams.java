package frontend.datastruct.ast;

import java.util.ArrayList;
import java.util.StringJoiner;

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
        StringJoiner sj = new StringJoiner(", ");
        for (AstExp exp : exps) {
            sj.add(exp.toString());
        }
        return sj.toString();
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
