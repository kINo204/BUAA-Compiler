package ir.datastruct;

import datastruct.ast.*;
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
                function.appendInstr(Instr.genReturn(val));
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
            return fromMulExp(addExp.mulExps.get(0));
        } else {
            final ArrayList<Operand> values = new ArrayList<>();
            for (AstMulExp mulExp : addExp.mulExps) {
                values.add(fromMulExp(mulExp));
            }
            Operand sum = values.get(0);
            for (int i = 1; i < values.size(); i++) {
                final Operand next = values.get(i);
                Reg calcRes = new Reg();
                Instr.Operator op = addExp.operators.get(i - 1) ==
                        Token.TokenId.PLUS ? Instr.Operator.ADD : Instr.Operator.SUB;
                function.appendInstr(Instr.genCalc(op, calcRes, sum, next));
                sum = calcRes;
            }
            return sum;
        }
    }

    private Operand fromMulExp(AstMulExp mulExp) {
        if (mulExp.unaryExps.size() == 1) {
            return fromUnaryExp(mulExp.unaryExps.get(0));
        } else {
            return null;
        }
    }

    private Operand fromUnaryExp(AstUnaryExp unaryExp) {
        if (unaryExp instanceof AstUnaryExpPrimary u) {
            return fromPrimaryExp(u.primaryExp);
        }
        return null;
    }

    private void fromUnaryOp(AstUnaryOp unaryOp) {}

    private void fromFuncRParams(AstFuncRParams funcRParams) {}

    private Operand fromPrimaryExp(AstPrimaryExp primaryExp) {
        if (primaryExp.number != null) {
            return fromNumber(primaryExp.number);
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
    private void fromCharacter(AstCharacter character) {}
}
