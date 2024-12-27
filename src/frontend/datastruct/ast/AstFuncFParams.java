package frontend.datastruct.ast;

import java.util.ArrayList;
import java.util.StringJoiner;

public class AstFuncFParams extends AstNode{
    public AstFuncFParams() {
        super(AstNodeId.FuncFParams);
    }

    public final ArrayList<AstFuncFParam> funcFParams = new ArrayList<>();

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        for (AstFuncFParam funcFParam : funcFParams) {
            sj.add(funcFParam.toString());
        }
        return sj.toString();
    }

    public void addFuncFParam(AstFuncFParam funcFParam) {
        funcFParams.add(funcFParam);
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
