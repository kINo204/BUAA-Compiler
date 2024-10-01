package datastruct.ast;

import java.util.ArrayList;

public class AstFuncFParams extends AstNode{
    public AstFuncFParams() {
        super(AstNodeId.FuncFParams);
    }

    private final ArrayList<AstFuncFParam> funcFParams = new ArrayList<>();

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
