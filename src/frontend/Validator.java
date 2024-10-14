package frontend;

import datastruct.ast.*;
import datastruct.symbol.SymConstVar;
import datastruct.symbol.SymFunc;
import datastruct.symbol.Symbol;
import datastruct.symbol.Symbol.SymId;
import static datastruct.symbol.Symbol.SymId.*;
import datastruct.symtbl.SymTbl;
import io.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class Validator {
    private final AstCompUnit ast;
    public final SymTbl symTbl;
    private final Log e;

    public Validator(AstCompUnit ast, Log o, Log e) {
        this.ast = ast;
        symTbl = new SymTbl(o, e);
        this.e = e;
    }

    public AstCompUnit getAst() {
        return ast;
    }

    public void validateAst() throws IOException {
        validate(ast);
    }

    private void validate(AstCompUnit compUnit) throws IOException {
        for (AstDecl decl : compUnit.decls)
            validate(decl);
        for (AstFuncDef funcDef: compUnit.funcDefs)
            validate(funcDef);
        validate(compUnit.mainFuncDef);
    }

    private void validate(AstMainFuncDef mainFuncDef) throws IOException {
        symTbl.pushScope(false, SymId.IntFunc);
        validate(mainFuncDef.block, false, false);

        // Should contain return statement.
        if (mainFuncDef.block.blockItems.isEmpty()
                || !(mainFuncDef.block.blockItems.get(
                mainFuncDef.block.blockItems.size() - 1
        ).content instanceof AstStmtReturn)) {
            e.error(mainFuncDef.block.braceEnd.lineNo, "g");
        }

        symTbl.exitScope();
    }

    private void validate(AstDecl decl) throws IOException {
        if (decl.content instanceof AstConstDecl) {
            validate((AstConstDecl) decl.content);
        } else {
            validate((AstVarDecl) decl.content);
        }
    }

    private void validate(AstVarDecl varDecl) throws IOException {
        for (AstVarDef varDef : varDecl.varDefs)
            validate(varDef);
    }

    private void validate(AstVarDef varDef) throws IOException {
        symTbl.addSymbol(Symbol.from(varDef));
        if (varDef.constExp != null)
            validate(varDef.constExp);
        if (varDef.initVal != null)
            validate(varDef.initVal);
    }

    private void validate(AstInitVal initVal) throws IOException {
        if (initVal.exp != null)
            validate(initVal.exp);
        else {
            for (AstExp exp : initVal.exps)
                validate(exp);
        }
    }

    private void validate(AstExp exp) throws IOException {
        validate(exp.astAddExp);
    }

    private void validate(AstConstExp constExp) throws IOException {
        validate(constExp.astAddExp);
    }

    private void validate(AstAddExp addExp) throws IOException {
        for (AstMulExp mulExp : addExp.mulExps) {
            for (AstUnaryExp unaryExp : mulExp.unaryExps) {
                validate(unaryExp);
            }
        }
    }

    private void validate(AstUnaryExp unaryExp) throws IOException {
        if (unaryExp instanceof AstUnaryExpPrimary s) {
            validate(s.primaryExp);
        } else if (unaryExp instanceof AstUnaryExpUnaryOp s) {
            validate(s.unaryExp);
        } else if (unaryExp instanceof AstUnaryExpFuncCall s) {
            validate(s.funcIdent, true);

            // Validating params types.
            SymFunc sym = (SymFunc) symTbl.searchSym(s.funcIdent);
            if (sym != null) {
                final ArrayList<SymId> fParams = sym.types;
                if (s.funcRParams == null) {
                    if (!fParams.isEmpty()) {
                        e.error(s.funcIdent.lineNo, "d");
                    }
                    return;
                }

                final ArrayList<AstExp> rParams = s.funcRParams.exps;
                for (AstExp exp : rParams) {
                    validate(exp);
                }
                if (fParams.size() != rParams.size()) {
                    e.error(s.funcIdent.lineNo, "d");
                }
                for (int i = 0; i < Math.min(fParams.size(), rParams.size()); i++) {
                    SymId fp = fParams.get(i);
                    SymId rp = typeof(rParams.get(i));
                    if (fp == IntArray || fp == CharArray) {
                        if (rp == Int || rp == Char) {
                            e.error(s.funcIdent.lineNo, "e");
                        } else if (
                                (fp == IntArray && rp == CharArray)
                                        || (fp == CharArray && rp == IntArray)
                        ) {
                            e.error(s.funcIdent.lineNo, "e");
                        }
                    } else {
                        if (rp == IntArray || rp == CharArray) {
                            e.error(s.funcIdent.lineNo, "e");
                        }
                    }
                }
            }

        }
    }

    private void validate(AstPrimaryExp primaryExp) throws IOException {
        if (primaryExp.bracedExp != null) {
            validate(primaryExp.bracedExp);
        } else if (primaryExp.lVal != null) {
            validate(primaryExp.lVal);
        }
    }

    private void validate(AstConstDecl constDecl) throws IOException {
        for (AstConstDef constDef : constDecl.constDefs)
            validate(constDef);
    }

    private void validate(AstConstDef constDef) throws IOException {
        symTbl.addSymbol(Symbol.from(constDef));
        if (constDef.constExp != null)
            validate(constDef.constExp);
        validate(constDef.constInitVal);
    }

    private void validate(AstConstInitVal constInitVal) throws IOException {
        if (constInitVal.constExp != null)
            validate(constInitVal.constExp);
        else {
            for (AstConstExp constExp : constInitVal.constExps)
                validate(constExp);
        }
    }

    private void validate(AstFuncDef funcDef) throws IOException {
        Symbol function = Symbol.from(funcDef);
        symTbl.addSymbol(function);
        symTbl.pushScope(false, function.symId);

        if (funcDef.funcFParams != null) {
            for (AstFuncFParam param : funcDef.funcFParams.funcFParams)
                symTbl.addSymbol(Symbol.from(param));
        }
        validate(funcDef.block, false, false);

        if (function.symId != SymId.VoidFunc) {
            // Should contain return statement.
            if (funcDef.block.blockItems.isEmpty()
                    || !(funcDef.block.blockItems.get(
                    funcDef.block.blockItems.size() - 1
            ).content instanceof AstStmtReturn)) {
                e.error(funcDef.block.braceEnd.lineNo, "g");
            }
        }

        symTbl.exitScope();
    }

    private void validate(AstBlock block, boolean scoped, boolean enterLoop) throws IOException {
        if (scoped) symTbl.pushScope(enterLoop, null);

        for (AstBlockItem blockItem : block.blockItems)
            validate(blockItem);

        if (scoped) symTbl.exitScope();
    }

    private void validate(AstBlockItem blockItem) throws IOException {
        if (blockItem.content instanceof AstDecl)
            validate((AstDecl) blockItem.content);
        else
            validate((AstStmt) blockItem.content, false);
    }

    private void validate(AstStmt stmt, boolean enterLoop) throws IOException {
        if (stmt instanceof AstStmtAssign s) {
            validate(s.lVal); // Redefinition and not defined error eliminated here.
            validate(s.exp);
            if (symTbl.searchSym(s.lVal.ident) instanceof SymConstVar)
                e.error(s.lVal.ident.lineNo, "h");
        } else if (stmt instanceof AstStmtSingleExp s) {
            if (s.exp != null)
                validate(s.exp);
        } else if (stmt instanceof AstStmtGetint s) {
            validate(s.lVal);
            if (symTbl.searchSym(s.lVal.ident) instanceof SymConstVar)
                e.error(s.lVal.ident.lineNo, "h");
        } else if (stmt instanceof AstStmtGetchar s) {
            validate(s.lVal);
        } else if (stmt instanceof AstStmtBlock s) {
            validate(s.block, true, enterLoop);
        } else if (stmt instanceof AstStmtIf s) {
            validate(s.cond);
            validate(s.ifStmt, false);
            if (s.elseStmt != null)
                validate(s.elseStmt, false);
        } else if (stmt instanceof AstStmtFor s) {
            if (s.firstForStmt != null) {
                validate(s.firstForStmt.lVal);
                validate(s.firstForStmt.exp);
            }
            if (s.cond != null)
                validate(s.cond);
            if (s.thirdForStmt != null) {
                validate(s.thirdForStmt.lVal);
                validate(s.thirdForStmt.exp);
            }
            validate(s.stmt, true);
        } else if (stmt instanceof AstStmtBreak s) {
            if (!symTbl.inLoop()) {
                e.error(s.token.lineNo, "m");
            }
        } else if (stmt instanceof AstStmtContinue s) {
            if (!symTbl.inLoop()) {
                e.error(s.token.lineNo, "m");
            }
        } else if (stmt instanceof AstStmtReturn s) {
            if (s.exp != null && symTbl.curFuncEnv() == SymId.VoidFunc) {
                e.error(s.returnTk.lineNo, "f");
            }
        } else if (stmt instanceof AstStmtPrintf s) {
            validate(s);
        }
    }

    private void validate(AstStmtPrintf stmtPrintf) throws IOException {
        final int nExp = stmtPrintf.exps.size();
        final int nFmt = ("#" + stmtPrintf.stringConst.val.string + "$").split("%d|%c").length - 1;
        if (nExp != nFmt)
            e.error(stmtPrintf.printfTk.lineNo, "l");
        for (AstExp exp : stmtPrintf.exps) {
            validate(exp);
        }
    }

    private void validate(AstCond cond) throws IOException {
        validate(cond.lOrExp);
    }

    private void validate(AstLOrExp lOrExp) throws IOException {
        for (AstLAndExp lAndExp : lOrExp.lAndExps) {
            for (AstEqExp eqExp : lAndExp.eqExps) {
                for (AstRelExp relExp : eqExp.relExps) {
                    for (AstAddExp addExp: relExp.addExps) {
                        validate(addExp);
                    }
                }
            }
        }
    }

    private void validate(AstLVal lVal) throws IOException {
        validate(lVal.ident, false);
        if (lVal.exp != null)
            validate(lVal.exp);
    }

    private void validate(Token ident, boolean isFuncCall) {
        final Symbol s = symTbl.searchSym(ident);
        if (s == null)
            e.error(ident.lineNo, "c");
        else if (s instanceof SymFunc && !isFuncCall) {
            e.error(ident.lineNo, "c");
        } else if (!(s instanceof SymFunc) && isFuncCall) {
            e.error(ident.lineNo, "c");
        }
    }

    // Type "checker" and calculator.
    /* As no error for type mismatching required, we'll simply panic on mismatch. */
    private SymId typeof(AstExp exp) {
        return typeof(exp.astAddExp);
    }

    private SymId typeof(AstConstExp constExp) {
        return typeof(constExp.astAddExp);
    }

    private SymId typeof(AstAddExp addExp) {
        if (addExp.mulExps.size() == 1) {
            return typeof(addExp.mulExps.get(0));
        } else {
            boolean isConst = true;
            for (AstMulExp mulExp: addExp.mulExps) {
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
        if (primaryExp.bracedExp != null) {
            return typeof(primaryExp.bracedExp);
        } else if (primaryExp.lVal != null) {
            AstLVal lVal = primaryExp.lVal;
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
        } else if (primaryExp.character != null) {
            return SymId.Char;
        } else if (primaryExp.number != null) {
            return SymId.Int;
        } else {
            assert false; // Invalid token!
            return null;
        }
    }
}
