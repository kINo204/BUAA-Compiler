package ir;

import frontend.datastruct.ast.*;
import frontend.datastruct.symbol.SymConstVar;
import frontend.datastruct.symbol.SymFunc;
import frontend.datastruct.symbol.SymVar;
import frontend.datastruct.symbol.Symbol;
import frontend.datastruct.symtbl.SymTbl;
import ir.datastruct.Function;
import ir.datastruct.Instr;
import ir.datastruct.Ir;
import ir.datastruct.Module;
import ir.datastruct.operand.*;

import java.util.ArrayList;
import java.util.Arrays;

import static ir.datastruct.Instr.Type.*;

public class IrMaker {
    private final AstCompUnit tree;
    private final SymTbl symTbl;
    private Ir ir;

    public IrMaker(AstCompUnit tree, SymTbl symTbl) {
        this.tree = tree;
        this.symTbl = symTbl;
        symTbl.initVisits();
    }

    public Ir make() {
        ir = new Ir();
        fromCompUnit(tree);
        return ir;
    }

    // Calling contract: on calling "fromXXX", parts of IR for XXX will be generated.
    private void fromCompUnit(AstCompUnit compUnit) {
        ir.module = new Module();
        // TODO global decl
        for (AstFuncDef funcDef : compUnit.funcDefs) {
            fromFuncDef(funcDef, ir.module);
        }
        fromMainFuncDef(compUnit.mainFuncDef, ir.module);
    }

    private void fromFuncDef(AstFuncDef funcDef, Module module) {
        Function function = new Function((SymFunc) symTbl.searchSym(funcDef.ident));
        Reg.reset();
        symTbl.enterScope();

        fromBlock(funcDef.block, function, false);

        symTbl.exitScope();
        module.functions.add(function);
    }

    private void fromMainFuncDef(AstMainFuncDef mainFuncDef, Module module) {
        Function function = new Function(true);
        Reg.reset();
        symTbl.enterScope();

        fromBlock(mainFuncDef.block, function, false);

        symTbl.exitScope();
        module.functions.add(function);
    }

    private void fromBlock(AstBlock block, Function function, boolean enterScope) {
        if (enterScope) symTbl.enterScope();

        for (AstBlockItem blockItem : block.blockItems) {
            fromBlockItem(blockItem, function);
        }

        if (enterScope) symTbl.exitScope();
    }

    private void fromBlockItem(AstBlockItem blockItem, Function function) {
        if (blockItem.content instanceof AstDecl c) {
            fromDecl(c, function);
        } else {
            fromStmt((AstStmt) blockItem.content, function);
        }
    }

    private void fromDecl(AstDecl decl, Function function) {
        if (decl.content instanceof AstVarDecl varDecl) {
            fromVarDecl(varDecl, function);
        } else {
            AstConstDecl constDecl = (AstConstDecl) decl.content;
            fromConstDecl(constDecl, function);
        }
    }

    private ArrayList<Const> getStringConstPieces(String str) {
        ArrayList<Const> pieces = new ArrayList<>();

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\\') {
                char next = str.charAt(i + 1);
                assert Arrays.asList('a', 'b', 't', 'n', 'v', 'f', '\"', '\'', '\\', '0')
                        .contains(next);
                Const charConst = new Const(switch (next) {
                    case 'a' -> 7;
                    case 'b' -> 8;
                    case 't' -> 9;
                    case 'n' -> 10;
                    case 'v' -> 11;
                    case 'f' -> 12;
                    case '\"' -> 34;
                    case '\'' -> 39;
                    case '\\' -> 92;
                    case '0' -> 0;
                    default -> throw new IllegalStateException(
                            "Unexpected escaping char: " + str.charAt(i + 1));
                });
                i++;
                pieces.add(charConst);
            } else {
                pieces.add(new Const(ch));
            }
        }

        // The "\0".
        pieces.add(new Const('\0'));

        return pieces;
    }

    private void fromConstDecl(AstConstDecl constDecl, Function function) {
        for (AstConstDef def : constDecl.constDefs) {
            SymConstVar varSymbol = (SymConstVar) symTbl.searchSym(def.ident);
            Var var = new Var(varSymbol);
            varSymbol.irVar = var;

            if (var.isArray) {
                // TODO We must fold this `constExp` at compile time! Then the
                //      operand will be just a `Const` type.
                //      `def.constExp = Calculator.calc(def.constExp);`
                //      or fold all in Validator.
                Operand arrayLength = fromConstExp(def.constExp, function);
                var.arrayLength = ((Const) arrayLength).num;
                function.appendInstr(Instr.genAlloc(var, arrayLength));

                final ArrayList<Operand> initVals = new ArrayList<>();
                if (def.constInitVal.stringConst == null) {
                    for (AstConstExp constExp : def.constInitVal.constExps) {
                        initVals.add(fromConstExp(constExp, function));
                    }
                    for (int i = 0; i < var.arrayLength; i++) {
                        if (i < initVals.size()) {
                            function.appendInstr(Instr.genStore(var, initVals.get(i), new Const(i)));
                        } else {
                            // For constArray, init other vals to 0.
                            function.appendInstr(Instr.genStore(var, new Const(0), new Const(i)));
                        }
                    }
                } else { // string const
                    String strInitVal = def.constInitVal.stringConst.val.string;
                    ArrayList<Const> charConsts = getStringConstPieces(strInitVal);
                    initVals.addAll(charConsts);
                    for (int i = 0; i < var.arrayLength; i++) {
                        if (i < initVals.size()) {
                            function.appendInstr(Instr.genStore(var, initVals.get(i), new Const(i)));
                        } else {
                            // For constArray, init other vals to 0.
                            function.appendInstr(Instr.genStore(var, new Const(0), new Const(i)));
                        }
                    }
                }
            } else {
                function.appendInstr(Instr.genAlloc(var));

                Operand initVal = fromConstExp(def.constInitVal.constExp, function);
                function.appendInstr(Instr.genStore(var, initVal));
            }
        }
    }

    private void fromVarDecl(AstVarDecl varDecl, Function function) {
        for (AstVarDef def : varDecl.varDefs) {
            SymVar varSymbol = (SymVar) symTbl.searchSym(def.ident);
            Var var = new Var(varSymbol);
            varSymbol.irVar = var;

            if (var.isArray) {
                // TODO We must fold this `constExp` at compile time! Then the
                //      operand will be just a `Const` type.
                //      `def.constExp = Calculator.calc(def.constExp);`
                //      or fold all in Validator.
                Operand arrayLength = fromConstExp(def.constExp, function);
                var.arrayLength = ((Const) arrayLength).num;
                function.appendInstr(Instr.genAlloc(var, arrayLength));

                if (def.initVal != null) {
                    final ArrayList<Operand> initVals = new ArrayList<>();
                    if (def.initVal.stringConst == null) {
                        for (AstExp exp : def.initVal.exps) {
                            initVals.add(fromExp(exp, function));
                        }
                        for (int i = 0; i < initVals.size(); i++) {
                            function.appendInstr(Instr.genStore(var, initVals.get(i), new Const(i)));
                        }
                    } else { // string const
                        String strInitVal = def.initVal.stringConst.val.string;
                        ArrayList<Const> charConsts = getStringConstPieces(strInitVal);
                        initVals.addAll(charConsts);
                        for (int i = 0; i < var.arrayLength; i++) {
                            if (i < initVals.size()) {
                                function.appendInstr(Instr.genStore(var, initVals.get(i), new Const(i)));
                            } else {
                                // For constArray, init other vals to 0.
                                function.appendInstr(Instr.genStore(var, new Const(0), new Const(i)));
                            }
                        }
                    }
                }
            } else {
                function.appendInstr(Instr.genAlloc(var));

                if (def.initVal != null) {
                    Operand initVal = fromExp(def.initVal.exp, function);
                    function.appendInstr(Instr.genStore(var, initVal));
                }
            }
        }
    }

    private void fromStmt(AstStmt stmt, Function function) {
        // TODO getint
        // TODO getchar
        if (stmt instanceof AstStmtSingleExp stmtSingleExp) {
            if (stmtSingleExp.exp != null) {
                fromExp(stmtSingleExp.exp, function);
            }
        } else if (stmt instanceof AstStmtAssign stmtAssign) {
            Operand value = fromExp(stmtAssign.exp, function);
            SymVar varSym = (SymVar) symTbl.searchSym(stmtAssign.lVal.ident);
            Var var = varSym.irVar;
            if (var.isArray) {
                Operand arrayIndex = fromExp(stmtAssign.lVal.exp, function);
                function.appendInstr(Instr.genStore(var, value, arrayIndex));
            } else {
                function.appendInstr(Instr.genStore(var, value));
            }
        } else if (stmt instanceof AstStmtBlock stmtBlock) {
            fromBlock(stmtBlock.block, function, true);
        } else if (stmt instanceof AstStmtReturn stmtReturn) {
            if (stmtReturn.exp == null) {
                function.appendInstr(Instr.genReturn());
            } else {
                Operand val = fromExp(stmtReturn.exp, function);
                function.appendInstr(Instr.genReturn(val));
            }
        } else if (stmt instanceof AstStmtIf stmtIf) {
            if (stmtIf.elseStmt == null) {
                Label labelEnd = new Label("if_end");

                Operand cond = fromCond(stmtIf.cond, function);
                function.appendInstr(Instr.genGoIfNot(labelEnd, cond));
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
                Label labelElse = new Label("if_else");
                Label labelEnd = new Label("if_end");

                Operand cond = fromCond(stmtIf.cond, function);
                function.appendInstr(Instr.genGoIfNot(labelElse, cond));
                fromStmt(stmtIf.ifStmt, function);
                function.appendInstr(Instr.genGoto(labelEnd));
                function.appendInstr(Instr.genLabelDecl(labelElse));
                fromStmt(stmtIf.elseStmt, function);
                function.appendInstr(Instr.genLabelDecl(labelEnd));
            }
        } else if (stmt instanceof AstStmtFor stmtFor) {
            Label forCond = new Label("for_cond"),
                    forMotion = new Label("for_motion"),
                    forEnd = new Label("for_end");
            symTbl.setLoopLabels(forMotion, forEnd);

            if (stmtFor.firstForStmt != null) {
                fromForStmt(stmtFor.firstForStmt, function);
            }

            function.appendInstr(Instr.genLabelDecl(forCond));

            if (stmtFor.cond != null) {
                Operand cond = fromCond(stmtFor.cond, function);
                function.appendInstr(Instr.genGoIfNot(forEnd, cond));
            }


            fromStmt(stmtFor.stmt, function);

            function.appendInstr(Instr.genLabelDecl(forMotion));

            if (stmtFor.thirdForStmt != null) {
                fromForStmt(stmtFor.thirdForStmt, function);
            }

            function.appendInstr(Instr.genGoto(forCond));
            function.appendInstr(Instr.genLabelDecl(forEnd));
        } else if (stmt instanceof AstStmtBreak) {
            Label forEnd = symTbl.getCurLoopEnd();
            function.appendInstr(Instr.genGoto(forEnd));
        } else if (stmt instanceof AstStmtContinue) {
            Label forStart = symTbl.getCurLoopMotion();
            function.appendInstr(Instr.genGoto(forStart));
        } else if (stmt instanceof AstStmtPrintf funcCall) {
            String formatStr = funcCall.stringConst.val.string;
            int expInd = 0;
            for (int i = 0; i < formatStr.length(); i++) {
                char ch = formatStr.charAt(i);
                if (ch == '\\') {
                    assert formatStr.charAt(i + 1) == 'n';
                    genPutchar(new Const('\n'), function);
                    i++;
                } else if (ch != '%') {
                    genPutchar(new Const(ch), function);
                } else {
                    if (i + 1 >= formatStr.length() || (
                            formatStr.charAt(i + 1) != 'd'
                            && formatStr.charAt(i + 1) != 'c')
                    ) {
                        genPutchar(new Const(ch), function);
                    } else {
                        Operand operand = fromExp(funcCall.exps.get(expInd), function);
                        if (formatStr.charAt(i + 1) == 'c') {
                            genPutchar(operand, function);
                        } else {
                            genPutint(operand, function);
                        }
                        expInd++; i++;
                    }
                }
            }
        } else if (stmt instanceof AstStmtGetint stmtGetint) {
            Reg res = new Reg(FuncRef.frGetint().type);
            function.appendInstr(Instr.genFuncCall(res, FuncRef.frGetint()));

            SymVar varSym = (SymVar) symTbl.searchSym(stmtGetint.lVal.ident);
            Var var = varSym.irVar;
            if (var.isArray) {
                Operand arrayIndex = fromExp(stmtGetint.lVal.exp, function);
                function.appendInstr(Instr.genStore(var, res, arrayIndex));
            } else {
                function.appendInstr(Instr.genStore(var, res));
            }
        } else if (stmt instanceof AstStmtGetchar stmtGetchar) {
            Reg res = new Reg(FuncRef.frGetchar().type);
            function.appendInstr(Instr.genFuncCall(res, FuncRef.frGetchar()));

            SymVar varSym = (SymVar) symTbl.searchSym(stmtGetchar.lVal.ident);
            Var var = varSym.irVar;
            if (var.isArray) {
                Operand arrayIndex = fromExp(stmtGetchar.lVal.exp, function);
                function.appendInstr(Instr.genStore(var, res, arrayIndex));
            } else {
                function.appendInstr(Instr.genStore(var, res));
            }
        }
    }

    private void genPutchar(Operand param, Function function) {
        function.appendInstr(Instr.genParam(param));
        function.appendInstr(Instr.genFuncCall(FuncRef.frPutchar()));
    }

    private void genPutint(Operand param, Function function) {
        function.appendInstr(Instr.genParam(param));
        function.appendInstr(Instr.genFuncCall(FuncRef.frPutint()));
    }

    private void fromForStmt(AstForStmt forStmt, Function function) {
        Operand value = fromExp(forStmt.exp, function);
        SymVar varSym = (SymVar) symTbl.searchSym(forStmt.lVal.ident);
        Var var = varSym.irVar;
        if (var.isArray) {
            Operand arrayIndex = fromExp(forStmt.lVal.exp, function);
            function.appendInstr(Instr.genStore(var, value, arrayIndex));
        } else {
            function.appendInstr(Instr.genStore(var, value));
        }
    }

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
            Label labelTrue = new Label("lorexp_true");
            Label labelEnd = new Label("lorexp_end");

            for (AstLAndExp lAndExp : lOrExp.lAndExps) {
                Operand operand = fromLAndExp(lAndExp, function);
                Instr instrGoif = Instr.genGoif(labelTrue, operand);
                function.appendInstr(instrGoif);
            }

            Reg res = new Reg(i32);
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
            Label labelFalse = new Label("landexp_false");
            Label labelEnd = new Label("landexp_end");

            for (AstEqExp eqExp : lAndExp.eqExps) {
                Operand operand = fromEqExp(eqExp, function);
                Instr instrGont = Instr.genGoIfNot(labelFalse, operand);
                function.appendInstr(instrGont);
            }

            Reg res = new Reg(i32);
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
                Reg calcRes = new Reg(i32);
                Token.TokenId opToken = eqExp.operators.get(i - 1);
                Instr.Operator op = switch (opToken) {
                    case EQL -> Instr.Operator.EQL;
                    case NEQ -> Instr.Operator.NEQ;
                    default -> null; // ERROR
                };
                function.appendInstr(Instr.genCalc(op, calcRes, totRes, next));
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
                Reg calcRes = new Reg(i32);
                Token.TokenId opToken = relExp.operators.get(i - 1);
                Instr.Operator op = switch (opToken) {
                    case GRE -> Instr.Operator.GRE;
                    case GEQ -> Instr.Operator.GEQ;
                    case LSS -> Instr.Operator.LSS;
                    case LEQ -> Instr.Operator.LEQ;
                    default -> null; // ERROR
                };
                function.appendInstr(Instr.genCalc(op, calcRes, totRes, next));
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
                Reg calcRes = new Reg(i32);
                Instr.Operator op = addExp.operators.get(i - 1) ==
                        Token.TokenId.PLUS ? Instr.Operator.ADD : Instr.Operator.SUB;
                function.appendInstr(Instr.genCalc(op, calcRes, totRes, next));
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
                Reg calcRes = new Reg(i32);
                Instr.Operator op =
                        mulExp.operators.get(i - 1) == Token.TokenId.MULT ? Instr.Operator.MUL :
                        mulExp.operators.get(i - 1) == Token.TokenId.DIV  ? Instr.Operator.DIV :
                                Instr.Operator.MOD;
                function.appendInstr(Instr.genCalc(op, calcRes, totRes, next));
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
                    Reg res = new Reg(i32);
                    function.appendInstr(
                            Instr.genCalc(Instr.Operator.SUB,
                                    res, new Const(0), operandUnaryExp)
                    );
                    return res;
                }
                case NOT -> {
                    Reg res = new Reg(i32);
                    function.appendInstr(
                            Instr.genCalc(Instr.Operator.EQL,
                                    res, operandUnaryExp, new Const(0))
                    );
                    return res;
                }
                default -> {
                    /* PLUS contains no action. */
                    return operandUnaryExp;
                }
            }
        } else if (unaryExp instanceof AstUnaryExpFuncCall funcCall) {
            if (funcCall.funcRParams != null) {
                for (AstExp rParamExp : funcCall.funcRParams.exps) {
                    Operand rParam = fromExp(rParamExp, function);
                    function.appendInstr(Instr.genParam(rParam));
                }
            }
            FuncRef funcRef = ((SymFunc) symTbl.searchSym(funcCall.funcIdent)).funcRef;
            if (funcRef.type == VOID) {
                function.appendInstr(Instr.genFuncCall(funcRef));
            } else {
                Reg res = new Reg(funcRef.type);
                function.appendInstr(Instr.genFuncCall(res, funcRef));
                return res;
            }
        }
        return null;
    }

    private Operand fromPrimaryExp(AstPrimaryExp primaryExp, Function function) {
        if (primaryExp.bracedExp != null) {
            return fromExp(primaryExp.bracedExp, function);
        } else if (primaryExp.number != null) {
            return fromNumber(primaryExp.number);
        } else if (primaryExp.character != null) {
            return fromCharacter(primaryExp.character);
        } else if (primaryExp.lVal != null) {
            Symbol symbol = symTbl.searchSym(primaryExp.lVal.ident);
            Var var = symbol instanceof SymVar ? ((SymVar) symbol).irVar : ((SymConstVar) symbol).irVar;

            Reg res = new Reg(var.type);
            if (var.isArray) {
                if (primaryExp.lVal.exp == null) { // Use array base address
                    Reg addr = new Reg(i32);
                    function.appendInstr(Instr.genGetAddr(addr, var));
                    return addr;
                } else { // Load of array
                    Operand arrayIndex = fromExp(primaryExp.lVal.exp, function);
                    function.appendInstr(Instr.genLoad(res, var, arrayIndex));
                }
            } else if (var.isReference) {
                if (primaryExp.lVal.exp == null) { // No such grammar
                    assert false;
                } else { // Dereference of array base addr
                    Operand arrayIndex = fromExp(primaryExp.lVal.exp, function);
                    function.appendInstr(Instr.genDeref(res, var, arrayIndex));
                }
            } else {
                function.appendInstr(Instr.genLoad(res, var));
            }
            return res;
        } else {
            return null; // Ast error.
        }
    }

    // Return a constant operand.
    private Operand fromNumber(AstNumber number) {
        return new Const(number.intConst.val.integer);
    }

    // Return a constant operand.
    private Operand fromCharacter(AstCharacter character) {
        return new Const(character.charConst.val.character);
    }
}
