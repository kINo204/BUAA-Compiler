package frontend;

import datastruct.ast.*;
import datastruct.symbol.SymConstVar;
import datastruct.symbol.SymFunc;
import datastruct.symbol.Symbol;
import datastruct.symbol.Symbol.SymId;
import datastruct.symtbl.SymTbl;
import io.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static datastruct.symbol.Symbol.SymId.*;


public class Validator {
    // Inputs
    private final AstCompUnit ast;

    // Outputs
    public final SymTbl symTbl;

    // Components
    private final Log loggerErr;
    private final Type typer = new Type();


    public Validator(AstCompUnit ast, Log loggerErr) {
        this.ast = ast;
        symTbl = new SymTbl(loggerErr);
        this.loggerErr = loggerErr;
    }

    /* Validation Entry */
    public void validateAst() throws IOException {
        vCompUnit(ast);
    }

    private void vCompUnit(AstCompUnit compUnit) throws IOException {
        for (AstDecl decl : compUnit.decls)
            vDecl(decl);
        for (AstFuncDef funcDef : compUnit.funcDefs)
            vFuncDef(funcDef);
        vMainFuncDef(compUnit.mainFuncDef);
    }

    private void vFuncDef(AstFuncDef funcDef) throws IOException {
        // Ident DEF
        Symbol functionSym = Symbol.from(funcDef);
        symTbl.addSymbol(functionSym);

        // Enter function scope.
        symTbl.pushScope(false, functionSym.symId);

        // ( [ FuncFParams ] ) DEF
        if (funcDef.funcFParams != null) {
            for (AstFuncFParam param : funcDef.funcFParams.funcFParams)
                symTbl.addSymbol(Symbol.from(param));
        }

        // Block
        vBlock(funcDef.block, false, false);

        // Validate return stmt.
        if (functionSym.symId != SymId.VoidFunc) {
            // Should contain return statement.
            if (funcDef.block.blockItems.isEmpty() // no stmt at all
                    || ! (funcDef.block.blockItems.get(funcDef.block.blockItems.size() - 1)
                    .content instanceof AstStmtReturn) // not retStmt
            ) {
                loggerErr.error(funcDef.block.braceEnd.lineNo, "g");
            }
        }

        symTbl.exitScope();
    }

    private void vMainFuncDef(AstMainFuncDef mainFuncDef) throws IOException {
        symTbl.pushScope(false, SymId.IntFunc);

        // Block
        vBlock(mainFuncDef.block, false, false);

        // Should contain return statement.
        if (mainFuncDef.block.blockItems.isEmpty()
                || !(mainFuncDef.block.blockItems.get(mainFuncDef.block.blockItems.size() - 1)
                .content instanceof AstStmtReturn)) {
            loggerErr.error(mainFuncDef.block.braceEnd.lineNo, "g");
        }

        symTbl.exitScope();
    }

    /**
     *
     * @param block The AstBlock node to be validated.
     * @param enterScope if the block should create scope on itself. If inside a function,
     *               this is set to false because the function handles the scope itself.
     * @param enterLoop true if this block starts a loop structure.
     */
    private void vBlock(AstBlock block, boolean enterScope, boolean enterLoop) throws IOException {
        if (enterScope) {
            symTbl.pushScope(enterLoop, null);
        }

        // 0..* BlockItem
        for (AstBlockItem blockItem : block.blockItems)
            vBlockItem(blockItem);

        if (enterScope) {
            symTbl.exitScope();
        }
    }

    private void vBlockItem(AstBlockItem blockItem) throws IOException {
        if (blockItem.content instanceof AstDecl)
            vDecl((AstDecl) blockItem.content);
        else
            vStmt((AstStmt) blockItem.content, false);
    }

    private void vDecl(AstDecl decl) throws IOException {
        if (decl.content instanceof AstConstDecl) {
            vConstDecl((AstConstDecl) decl.content);
        } else {
            vVarDecl((AstVarDecl) decl.content);
        }
    }

    private void vConstDecl(AstConstDecl constDecl) throws IOException {
        for (AstConstDef constDef : constDecl.constDefs)
            vConstDef(constDef);
    }

    private void vConstDef(AstConstDef constDef) throws IOException {
        symTbl.addSymbol(Symbol.from(constDef));
        if (constDef.constExp != null)
            vConstExp(constDef.constExp);
        vConstInitVal(constDef.constInitVal);
    }

    private void vConstInitVal(AstConstInitVal constInitVal) throws IOException {
        if (constInitVal.constExp != null)
            vConstExp(constInitVal.constExp);
        else {
            for (AstConstExp constExp : constInitVal.constExps)
                vConstExp(constExp);
        }
    }

    private void vVarDecl(AstVarDecl varDecl) throws IOException {
        for (AstVarDef varDef : varDecl.varDefs)
            vVarDef(varDef);
    }

    private void vVarDef(AstVarDef varDef) throws IOException {
        symTbl.addSymbol(Symbol.from(varDef));
        if (varDef.constExp != null)
            vConstExp(varDef.constExp);
        if (varDef.initVal != null)
            vInitVal(varDef.initVal);
    }

    private void vInitVal(AstInitVal initVal) throws IOException {
        if (initVal.exp != null)
            vExp(initVal.exp);
        else {
            for (AstExp exp : initVal.exps)
                vExp(exp);
        }
    }

    private void vStmt(AstStmt stmt, boolean enterLoop) throws IOException {
        if (stmt instanceof AstStmtAssign s) {
            vLVal(s.lVal); // Undefined error eliminated here.
            vExp(s.exp);
            if (symTbl.searchSym(s.lVal.ident) instanceof SymConstVar)
                loggerErr.error(s.lVal.ident.lineNo, "h");

        } else if (stmt instanceof AstStmtSingleExp s) {
            if (s.exp != null)
                vExp(s.exp);

        } else if (stmt instanceof AstStmtGetint s) {
            vLVal(s.lVal);
            if (symTbl.searchSym(s.lVal.ident) instanceof SymConstVar)
                loggerErr.error(s.lVal.ident.lineNo, "h");

        } else if (stmt instanceof AstStmtGetchar s) {
            vLVal(s.lVal);
            if (symTbl.searchSym(s.lVal.ident) instanceof SymConstVar)
                loggerErr.error(s.lVal.ident.lineNo, "h");

        } else if (stmt instanceof AstStmtBlock s) {
            vBlock(s.block, true, enterLoop);

        } else if (stmt instanceof AstStmtIf s) {
            vCond(s.cond);
            vStmt(s.ifStmt, false);
            if (s.elseStmt != null)
                vStmt(s.elseStmt, false);

        } else if (stmt instanceof AstStmtFor s) {
            if (s.firstForStmt != null) {
                vLVal(s.firstForStmt.lVal);
                vExp(s.firstForStmt.exp);
                if (symTbl.searchSym(s.firstForStmt.lVal.ident) instanceof SymConstVar)
                    loggerErr.error(s.firstForStmt.lVal.ident.lineNo, "h");
            }
            if (s.cond != null)
                vCond(s.cond);
            if (s.thirdForStmt != null) {
                vLVal(s.thirdForStmt.lVal);
                vExp(s.thirdForStmt.exp);
                if (symTbl.searchSym(s.thirdForStmt.lVal.ident) instanceof SymConstVar)
                    loggerErr.error(s.thirdForStmt.lVal.ident.lineNo, "h");
            }
            vStmt(s.stmt, true);

        } else if (stmt instanceof AstStmtBreak s) {
            if (!symTbl.inLoop()) {
                loggerErr.error(s.token.lineNo, "m");
            }

        } else if (stmt instanceof AstStmtContinue s) {
            if (!symTbl.inLoop()) {
                loggerErr.error(s.token.lineNo, "m");
            }

        } else if (stmt instanceof AstStmtReturn s) {
            if (s.exp != null && symTbl.curFuncEnv() == SymId.VoidFunc) {
                loggerErr.error(s.returnTk.lineNo, "f");
            }
            if (s.exp != null) {
                vExp(s.exp);
            }
        } else if (stmt instanceof AstStmtPrintf s) {
            vStmtPrintf(s);
        }
    }

    private void vStmtPrintf(AstStmtPrintf stmtPrintf) throws IOException {
        final int nExp = stmtPrintf.exps.size();
        final int nFmt = ("#" + stmtPrintf.stringConst.val.string + "$").split("%d|%c").length - 1;
        if (nExp != nFmt)
            loggerErr.error(stmtPrintf.printfTk.lineNo, "l");
        for (AstExp exp : stmtPrintf.exps) {
            vExp(exp);
        }
    }

    private void vCond(AstCond cond) throws IOException {
        vLOrExp(cond.lOrExp);
    }

    private void vConstExp(AstConstExp constExp) throws IOException {
        vAddExp(constExp.astAddExp);
        constExp.type = typer.typeof(constExp);
    }

    private void vExp(AstExp exp) throws IOException {
        vAddExp(exp.addExp);
        exp.type = typer.typeof(exp);
    }

    private void vLOrExp(AstLOrExp lOrExp) throws IOException {
        for (AstLAndExp lAndExp : lOrExp.lAndExps) {
            for (AstEqExp eqExp : lAndExp.eqExps) {
                for (AstRelExp relExp : eqExp.relExps) {
                    for (AstAddExp addExp : relExp.addExps) {
                        vAddExp(addExp);
                    }
                }
            }
        }
    }

    private void vAddExp(AstAddExp addExp) throws IOException {
        for (AstMulExp mulExp : addExp.mulExps) {
            for (AstUnaryExp unaryExp : mulExp.unaryExps) {
                vUnaryExp(unaryExp);
            }
            mulExp.type = typer.typeof(mulExp);
        }
        addExp.type = typer.typeof(addExp);
    }

    private void vUnaryExp(AstUnaryExp unaryExp) throws IOException {
        if (unaryExp instanceof AstUnaryExpPrimary s) {
            vPrimaryExp(s.primaryExp);
        } else if (unaryExp instanceof AstUnaryExpUnaryOp s) {
            vUnaryExp(s.unaryExp);
        } else if (unaryExp instanceof AstUnaryExpFuncCall s) {
            vIdent(s.funcIdent, true);

            // Validating params types.
            SymFunc sym = (SymFunc) symTbl.searchSym(s.funcIdent);
            if (sym != null) {
                final ArrayList<SymId> fParams = sym.types;
                if (s.funcRParams == null) {
                    if (!fParams.isEmpty()) {
                        loggerErr.error(s.funcIdent.lineNo, "d");
                    }
                    return;
                }

                final ArrayList<AstExp> rParams = s.funcRParams.exps;
                for (AstExp exp : rParams) {
                    vExp(exp);
                }
                if (fParams.size() != rParams.size()) {
                    loggerErr.error(s.funcIdent.lineNo, "d");
                }
                for (int i = 0; i < Math.min(fParams.size(), rParams.size()); i++) {
                    SymId fp = fParams.get(i);
                    SymId rp = typer.typeof(rParams.get(i));
                    if (fp == IntArray || fp == CharArray) {
                        if (rp == Int || rp == Char) {
                            loggerErr.error(s.funcIdent.lineNo, "e");
                        } else if (
                                (fp == IntArray && rp == CharArray)
                                        || (fp == CharArray && rp == IntArray)
                        ) {
                            loggerErr.error(s.funcIdent.lineNo, "e");
                        }
                    } else {
                        if (rp == IntArray || rp == CharArray) {
                            loggerErr.error(s.funcIdent.lineNo, "e");
                        }
                    }
                }
            }
        }
        unaryExp.type = typer.typeof(unaryExp);
    }

    private void vPrimaryExp(AstPrimaryExp primaryExp) throws IOException {
        if (primaryExp.bracedExp != null) {
            vExp(primaryExp.bracedExp);
        } else if (primaryExp.lVal != null) {
            vLVal(primaryExp.lVal);
        }
        primaryExp.type = typer.typeof(primaryExp);
    }

    private void vLVal(AstLVal lVal) throws IOException {
        vIdent(lVal.ident, false);
        if (lVal.exp != null)
            vExp(lVal.exp);
        lVal.type = typer.typeof(lVal);
    }

    private void vIdent(Token ident, boolean isFuncCall) {
        final Symbol s = symTbl.searchSym(ident);
        if (s == null)
            loggerErr.error(ident.lineNo, "c");
        else if (s instanceof SymFunc && !isFuncCall) {
            loggerErr.error(ident.lineNo, "c");
        } else if (!(s instanceof SymFunc) && isFuncCall) {
            loggerErr.error(ident.lineNo, "c");
        }
    }

    private class Type {
        // Type "checker" and calculator.
        /* As no error for type mismatching required, we'll simply panic on mismatch. */
        private SymId typeof(AstExp exp) {
            if (exp.type != null) return exp.type;
            return typeof(exp.addExp);
        }

        private SymId typeof(AstConstExp constExp) {
            if (constExp.type != null) return constExp.type;
            return typeof(constExp.astAddExp);
        }

        private SymId typeof(AstAddExp addExp) {
            if (addExp.type != null) return addExp.type;

            if (addExp.mulExps.size() == 1) {
                return typeof(addExp.mulExps.get(0));
            } else {
                boolean isConst = true;
                for (AstMulExp mulExp : addExp.mulExps) {
                    SymId type = typeof(mulExp);
                    assert Arrays.asList(
                            SymId.Int,
                            SymId.Char,
                            SymId.ConstInt,
                            SymId.ConstChar).contains(type);
                    if (!Arrays.asList(
                                    SymId.ConstInt, SymId.ConstChar)
                            .contains(type)) {
                        isConst = false;
                        break;
                    }
                }
                if (isConst) {
                    return SymId.ConstInt;
                } else {
                    return SymId.Int;
                }
            }
        }

        private SymId typeof(AstMulExp mulExp) {
            if (mulExp.type != null) return mulExp.type;

            if (mulExp.unaryExps.size() == 1) {
                return typeof(mulExp.unaryExps.get(0));
            } else {
                boolean isConst = true;
                for (AstUnaryExp unaryExp : mulExp.unaryExps) {
                    SymId type = typeof(unaryExp);
                    assert Arrays.asList(
                            SymId.Int,
                            SymId.Char,
                            SymId.ConstInt,
                            SymId.ConstChar).contains(type);
                    if (!Arrays.asList(
                                    SymId.ConstInt, SymId.ConstChar)
                            .contains(type)) {
                        isConst = false;
                        break;
                    }
                }
                if (isConst) {
                    return SymId.ConstInt;
                } else {
                    return SymId.Int;
                }
            }
        }

        private SymId typeof(AstUnaryExp unaryExp) {
            if (unaryExp.type != null) return unaryExp.type;

            if (unaryExp instanceof AstUnaryExpPrimary p) {
                return typeof(p.primaryExp);
            } else if (unaryExp instanceof AstUnaryExpUnaryOp p) {
                SymId symId = typeof(p.unaryExp);
                assert Arrays.asList(
                        SymId.Int,
                        SymId.Char,
                        SymId.ConstInt,
                        SymId.ConstChar).contains(symId);
                return symId;
            } else if (unaryExp instanceof AstUnaryExpFuncCall p) {
                Symbol s = symTbl.searchSym(p.funcIdent);
                assert Arrays.asList(
                        SymId.IntFunc,
                        SymId.CharFunc).contains(s.symId);
                return switch (s.symId) {
                    case IntFunc -> SymId.Int;
                    case CharFunc -> SymId.Char;
                    default -> null;
                };
            } else {
                assert false; // invalid
                return null;
            }
        }

        private SymId typeof(AstPrimaryExp primaryExp) {
            if (primaryExp.type != null) return primaryExp.type;

            if (primaryExp.bracedExp != null) {
                return typeof(primaryExp.bracedExp);
            } else if (primaryExp.lVal != null) {
                return typeof(primaryExp.lVal);
            } else if (primaryExp.character != null) {
                return SymId.Char;
            } else if (primaryExp.number != null) {
                return SymId.Int;
            } else {
                assert false; // Invalid token!
                return null;
            }
        }

        private SymId typeof(AstLVal lVal) {
            if (lVal.type != null) return lVal.type;

            Symbol s = symTbl.searchSym(lVal.ident);
            if (lVal.exp == null) {
                assert !Arrays.asList(
                        SymId.VoidFunc,
                        SymId.IntFunc,
                        SymId.CharFunc).contains(s.symId);
                return s.symId;
            } else {
                assert Arrays.asList(
                        IntArray,
                        SymId.CharArray,
                        SymId.ConstIntArray,
                        SymId.ConstCharArray).contains(s.symId);
                return switch (s.symId) {
                    case IntArray -> SymId.Int;
                    case CharArray -> SymId.Char;
                    case ConstIntArray -> SymId.ConstInt;
                    case ConstCharArray -> SymId.ConstChar;
                    default -> null;
                };
            }
        }
    }
}
