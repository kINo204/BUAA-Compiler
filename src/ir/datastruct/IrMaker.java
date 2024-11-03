package ir.datastruct;

import datastruct.ast.*;
import datastruct.symbol.Symbol;
import datastruct.symtbl.SymTbl;
import ir.datastruct.operand.Const;
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
        function.createBasicBlock();
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
        if (stmt instanceof AstStmtReturn stmtReturn) {
            if (stmtReturn.exp == null) {
                function.appendInstr(Instr.genReturn());
            } else {
                Operand val = fromExp(stmtReturn.exp, function);
                function.appendInstr(Instr.genReturn(val, stmtReturn.exp.type));
            }
        }
    }

    private void fromForStmt(AstForStmt forStmt) {}

    private void fromCond(AstCond cond) {}

    private Operand fromExp(AstExp exp, Function function) {
        return fromAddExp(exp.addExp, function);
    }

    private void fromConstExp(AstConstExp constExp) {}

    private void fromLOrExp(AstLOrExp lOrExp) {}

    private void fromLAndExp(AstLAndExp lAndExp) {}

    private void fromEqExp(AstEqExp eqExp) {}

    private void fromRelExp(AstRelExp relExp) {}

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
            Reg res = new Reg();
            switch (u.unaryOp.op.tokenId) {
                /* PLUS contains no action. */
                case MINU -> function.appendInstr(
                        Instr.genCalc(Instr.Operator.SUB,
                                Symbol.SymId.Int, res, new Const(0), operandUnaryExp)
                );
                case NOT -> { /* TODO */ }
                default -> { }
            };
            return res;
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
