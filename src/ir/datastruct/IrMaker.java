package ir.datastruct;

import datastruct.ast.*;
import datastruct.symbol.Symbol;
import datastruct.symtbl.SymTbl;
import ir.datastruct.operand.Const;
import ir.datastruct.operand.Label;
import ir.datastruct.operand.Operand;
import ir.datastruct.operand.Reg;

import java.util.ArrayList;

public class IrMaker {
    private final AstCompUnit tree;
    private final SymTbl symTbl;
    private Ir ir;

    public IrMaker(AstCompUnit tree, SymTbl symTbl) {
        this.tree = tree;
        this.symTbl = symTbl;
    }

    public Ir make() {
        ir = new Ir();
        fromCompUnit(tree);
        return ir;
    }

    // Calling contract: on calling "fromXXX", parts of IR for XXX will be generated.
    private void fromCompUnit(AstCompUnit compUnit) {
        ir.module = new Module();
        // TODO other elements
        fromMainFuncDef(compUnit.mainFuncDef, ir.module);
    }

    private void fromFuncDef(AstFuncDef funcDef) {}

    private void fromFuncType(AstFuncType funcType) {}

    private void fromFuncFParams(AstFuncFParams funcFParams) {}

    private void fromFuncFParam(AstFuncFParam funcFParam) {}

    private void fromMainFuncDef(AstMainFuncDef mainFuncDef, Module module) {
        Function function = new Function(true);
        Reg.reset();
        fromBlock(mainFuncDef.block, function);
        module.functions.add(function);
    }

    private void fromBlock(AstBlock block, Function function) {
        for (AstBlockItem blockItem : block.blockItems) {
            fromBlockItem(blockItem, function);
        }
    }

    private void fromBlockItem(AstBlockItem blockItem, Function function) {
        if (blockItem.content instanceof AstDecl c) {
            fromDecl(c, function);
        } else {
            fromStmt((AstStmt) blockItem.content, function);
        }
    }

    private void fromDecl(AstDecl decl, Function function) {}

    private void fromConstDecl(AstConstDecl constDecl) {}

    private void fromConstDef(AstConstDef constDef) {}

    private void fromConstInitVal(AstConstInitVal constInitVal) {}

    private void fromVarDecl(AstVarDecl varDecl) {}

    private void fromVarDef(AstVarDef varDef) {}

    private void fromInitVal(AstInitVal initVal) {}

    private void fromStmt(AstStmt stmt, Function function) {
        if (stmt instanceof AstStmtBlock stmtBlock) {
            fromBlock(stmtBlock.block, function);
        } else if (stmt instanceof AstStmtReturn stmtReturn) {
            if (stmtReturn.exp == null) {
                function.appendInstr(Instr.genReturn());
            } else {
                Operand val = fromExp(stmtReturn.exp, function);
                function.appendInstr(Instr.genReturn(val, stmtReturn.exp.type));
            }
        } else if (stmt instanceof AstStmtIf stmtIf) {
            if (stmtIf.elseStmt == null) {
                Label labelEnd = new Label("if-end");

                Operand cond = fromCond(stmtIf.cond, function);
                function.appendInstr(Instr.genGoifnot(labelEnd, cond));
                fromStmt(stmtIf.ifStmt, function);
                function.appendInstr(Instr.genLabelDecl(labelEnd));
            } else {
                /*
                gont L-else, cond
                ... (ifStmt)
                goto L-end
                L-else:
                ... (elseStmt)
                L-end:
                 */
                Label labelElse = new Label("if-else");
                Label labelEnd = new Label("if-end");

                Operand cond = fromCond(stmtIf.cond, function);
                function.appendInstr(Instr.genGoifnot(labelElse, cond));
                fromStmt(stmtIf.ifStmt, function);
                function.appendInstr(Instr.genGoto(labelEnd));
                function.appendInstr(Instr.genLabelDecl(labelElse));
                fromStmt(stmtIf.elseStmt, function);
                function.appendInstr(Instr.genLabelDecl(labelEnd));
            }
        }
    }

    private void fromForStmt(AstForStmt forStmt) {}

    private Operand fromCond(AstCond cond, Function function) {
        return fromLOrExp(cond.lOrExp, function);
    }

    private Operand fromExp(AstExp exp, Function function) {
        return fromAddExp(exp.addExp, function);
    }

    private Operand fromConstExp(AstConstExp constExp, Function function) {
        return fromAddExp(constExp.addExp, function);
    }

    /* Example: if (1 || 2 || 3) { ... }
    {goif  true, 1
    goif  true, 2
    goif  true, 3
    %1 = 0
    goto  Lend
    true:
    %1 = 1
    Lend:}

    {goif  Lif-body, %1
    goto  Lif-end
    Lif-body:
    ...
    Lif-end:}
     */
    private Operand fromLOrExp(AstLOrExp lOrExp, Function function) {
        if (lOrExp.lAndExps.size() == 1) {
            return fromLAndExp(lOrExp.lAndExps.get(0), function);
        } else {
            Label labelTrue = new Label("lorexp-true");
            Label labelEnd = new Label("lorexp-end");

            for (AstLAndExp lAndExp : lOrExp.lAndExps) {
                Operand operand = fromLAndExp(lAndExp, function);
                Instr instrGoif = Instr.genGoif(labelTrue, operand);
                function.appendInstr(instrGoif);
            }

            Reg res = new Reg();
            function.appendInstr(Instr.genMove(new Const(0), res));
            function.appendInstr(Instr.genGoto(labelEnd));

            function.appendInstr(Instr.genLabelDecl(labelTrue));
            function.appendInstr(Instr.genMove(new Const(1), res));

            function.appendInstr(Instr.genLabelDecl(labelEnd));

            return res;
        }
    }

    private Operand fromLAndExp(AstLAndExp lAndExp, Function function) {
        if (lAndExp.eqExps.size() == 1) {
            return fromEqExp(lAndExp.eqExps.get(0), function);
        } else {
            Label labelFalse = new Label("landexp-false");
            Label labelEnd = new Label("landexp-end");

            for (AstEqExp eqExp : lAndExp.eqExps) {
                Operand operand = fromEqExp(eqExp, function);
                Instr instrGont = Instr.genGoifnot(labelFalse, operand);
                function.appendInstr(instrGont);
            }

            Reg res = new Reg();
            function.appendInstr(Instr.genMove(new Const(1), res));
            function.appendInstr(Instr.genGoto(labelEnd));

            function.appendInstr(Instr.genLabelDecl(labelFalse));
            function.appendInstr(Instr.genMove(new Const(0), res));

            function.appendInstr(Instr.genLabelDecl(labelEnd));

            return res;
        }
    }

    private Operand fromEqExp(AstEqExp eqExp, Function function) {
        if (eqExp.relExps.size() == 1) {
            return fromRelExp(eqExp.relExps.get(0), function);
        } else {
            final ArrayList<Operand> values = new ArrayList<>();
            for (AstRelExp relExp : eqExp.relExps) {
                values.add(fromRelExp(relExp, function));
            }
            Operand totRes = values.get(0);
            for (int i = 1; i < values.size(); i++) {
                final Operand next = values.get(i);
                Reg calcRes = new Reg();
                Token.TokenId opToken = eqExp.operators.get(i - 1);
                Instr.Operator op = switch (opToken) {
                    case EQL -> Instr.Operator.EQL;
                    case NEQ -> Instr.Operator.NEQ;
                    default -> null; // ERROR
                };
                function.appendInstr(Instr.genCalc(op, Symbol.SymId.Int, calcRes, totRes, next));
                totRes = calcRes;
            }
            return totRes;
        }
    }

    private Operand fromRelExp(AstRelExp relExp, Function function) {
        if (relExp.addExps.size() == 1) {
            return fromAddExp(relExp.addExps.get(0), function);
        } else {
            final ArrayList<Operand> values = new ArrayList<>();
            for (AstAddExp addExp : relExp.addExps) {
                values.add(fromAddExp(addExp, function));
            }
            Operand totRes = values.get(0);
            for (int i = 1; i < values.size(); i++) {
                final Operand next = values.get(i);
                Reg calcRes = new Reg();
                Token.TokenId opToken = relExp.operators.get(i - 1);
                Instr.Operator op = switch (opToken) {
                    case GRE -> Instr.Operator.GRE;
                    case GEQ -> Instr.Operator.GEQ;
                    case LSS -> Instr.Operator.LSS;
                    case LEQ -> Instr.Operator.LEQ;
                    default -> null; // ERROR
                };
                function.appendInstr(Instr.genCalc(op, Symbol.SymId.Int, calcRes, totRes, next));
                totRes = calcRes;
            }
            return totRes;
        }
    }

    private Operand fromAddExp(AstAddExp addExp, Function function) {
        if (addExp.mulExps.size() == 1) {
            return fromMulExp(addExp.mulExps.get(0), function);
        } else {
            final ArrayList<Operand> values = new ArrayList<>();
            for (AstMulExp mulExp : addExp.mulExps) {
                values.add(fromMulExp(mulExp, function));
            }
            Operand totRes = values.get(0);
            for (int i = 1; i < values.size(); i++) {
                final Operand next = values.get(i);
                Reg calcRes = new Reg();
                Instr.Operator op = addExp.operators.get(i - 1) ==
                        Token.TokenId.PLUS ? Instr.Operator.ADD : Instr.Operator.SUB;
                function.appendInstr(Instr.genCalc(op, addExp.type/* TODO: using total type here */, calcRes, totRes, next));
                totRes = calcRes;
            }
            return totRes;
        }
    }

    private Operand fromMulExp(AstMulExp mulExp, Function function) {
        if (mulExp.unaryExps.size() == 1) {
            return fromUnaryExp(mulExp.unaryExps.get(0), function);
        } else {
            final ArrayList<Operand> values = new ArrayList<>();
            for (AstUnaryExp unaryExp : mulExp.unaryExps) {
                values.add(fromUnaryExp(unaryExp, function));
            }
            Operand totRes = values.get(0);
            for (int i = 1; i < values.size(); i++) {
                final Operand next = values.get(i);
                Reg calcRes = new Reg();
                Instr.Operator op =
                        mulExp.operators.get(i - 1) == Token.TokenId.MULT ? Instr.Operator.MUL :
                        mulExp.operators.get(i - 1) == Token.TokenId.DIV  ? Instr.Operator.DIV :
                                Instr.Operator.MOD;
                function.appendInstr(Instr.genCalc(op, mulExp.type/* TODO: using total type here */, calcRes, totRes, next));
                totRes = calcRes;
            }
            return totRes;
        }
    }

    private Operand fromUnaryExp(AstUnaryExp unaryExp, Function function) {
        if (unaryExp instanceof AstUnaryExpPrimary u) {
            return fromPrimaryExp(u.primaryExp, function);
        } else if (unaryExp instanceof AstUnaryExpUnaryOp u) {
            Operand operandUnaryExp = fromUnaryExp(u.unaryExp, function);
            switch (u.unaryOp.op.tokenId) {
                case MINU -> {
                    Reg res = new Reg();
                    function.appendInstr(
                            Instr.genCalc(Instr.Operator.SUB,
                                    Symbol.SymId.Int, res, new Const(0), operandUnaryExp)
                    );
                    return res;
                }
                case NOT -> {
                    Reg res = new Reg();
                    function.appendInstr(
                            Instr.genCalc(Instr.Operator.EQL,
                                    Symbol.SymId.Int, res, operandUnaryExp, new Const(0))
                    );
                    return res;
                }
                default -> {
                    /* PLUS contains no action. */
                    return operandUnaryExp;
                }
            }
        }
        return null;
    }

    private void fromFuncRParams(AstFuncRParams funcRParams) {}

    private Operand fromPrimaryExp(AstPrimaryExp primaryExp, Function function) {
        if (primaryExp.bracedExp != null) {
            return fromExp(primaryExp.bracedExp, function);
        } else if (primaryExp.number != null) {
            return fromNumber(primaryExp.number);
        } else if (primaryExp.character != null) {
            return fromCharacter(primaryExp.character);
        }
        return null; // Ast error.
    }

    // Load LVal to a register.
    private void fromLVal(AstLVal lVal) {}

    // Return a constant operand.
    private Operand fromNumber(AstNumber number) {
        return new Const(number.intConst.val.integer);
    }

    // Return a constant operand.
    private Operand fromCharacter(AstCharacter character) {
        return new Const(character.charConst.val.character);
    }
}
