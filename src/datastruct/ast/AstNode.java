package datastruct.ast;

public abstract class AstNode {
    private final AstNodeId astNodeId;

    public AstNode(AstNodeId astNodeId) {
        this.astNodeId = astNodeId;
    }

    @SuppressWarnings("all")
    public enum AstNodeId {
        CompUnit,
        FuncDef,
        FuncType,
        FuncFParams,
        FuncFParam,
        MainFuncDef,
        Block,
        BlockItem,
        Decl,
        ConstDecl,
        ConstDef,
        ConstInitVal,
        VarDecl,
        VarDef,
        InitVal,
        Stmt,
        ForStmt,
        Cond,
        Exp,
        ConstExp,
        LOrExp,
        LAndExp,
        EqExp,
        RelExp,
        AddExp,
        MulExp,
        UnaryExp,
        UnaryOp,
        FuncRParams,
        PrimaryExp,
        LVal,
        Number,
        Character
    }

    public String output() {
        return "<" + astNodeId.toString() + ">";
    }

    public String buildTree(String style) {
        return switch (style) {
            case "brack" -> buildTreeBrack();
            case "console" -> buildTreeConsole();
            default -> buildTreeConsole();
        };
    }

    public abstract String buildTreeBrack();
    public abstract String buildTreeConsole();
}
