package frontend;

import frontend.datastruct.ast.*;
import frontend.datastruct.symbol.SymConstVar;
import frontend.datastruct.symbol.SymFunc;
import frontend.datastruct.symbol.SymVar;
import frontend.datastruct.symbol.Symbol;
import frontend.datastruct.symbol.Symbol.SymId;
import frontend.datastruct.symtbl.SymTbl;
import utils.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static frontend.datastruct.symbol.Symbol.SymId.*;


public class Validator {
    // Inputs
    private final AstCompUnit ast;

    // Outputs
    public final SymTbl symTbl;

    // Components
    private final Log loggerErr;
    private final Type typer = new Type();
    private final Evaluator evaluator = new Evaluator();

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
        SymFunc functionSym = (SymFunc) Symbol.from(funcDef);
        symTbl.addSymbol(functionSym);

        // Enter function scope.
        symTbl.pushScope(false, functionSym.symId);

        // ( [ FuncFParams ] ) DEF
        if (funcDef.funcFParams != null) {
            for (AstFuncFParam param : funcDef.funcFParams.funcFParams) {
                Symbol symParam = Symbol.from(param);
                symTbl.addSymbol(symParam);
                functionSym.addParamSym(symParam);
            }
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
        } else { // Add a "return" at tail for void function without one.
            if (funcDef.block.blockItems.isEmpty()
                    || !(funcDef.block.blockItems.get(funcDef.block.blockItems.size() - 1).content
                    instanceof AstStmtReturn)) {
                AstBlockItem blockItem = new AstBlockItem();
                blockItem.content = new AstStmtReturn();
                funcDef.block.blockItems.add(blockItem);
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
        SymConstVar symConstVar = (SymConstVar) Symbol.from(constDef);
        symTbl.addSymbol(symConstVar);
        if (constDef.constExp != null) {
            vConstExp(constDef.constExp);
            // Try folding this.
            AstConstExp newConstExp = new AstConstExp();
            newConstExp.addExp = evaluator.buildAddExp(evaluator.evaluate(constDef.constExp));
            constDef.constExp = newConstExp;
        }
        vConstInitVal(constDef.constInitVal, symConstVar);
        if (constDef.constExp != null) {
            int len = (int) evaluator.evaluate(constDef.constExp);
            while (symConstVar.values.size() < len) {
                symConstVar.values.add('\0');
            }
        }
    }

    private void vConstInitVal(AstConstInitVal constInitVal, SymConstVar symConstVar) throws IOException {
        if (constInitVal.constExp != null) {
            vConstExp(constInitVal.constExp);

            // Try folding this.
            AstConstExp newConstExp = new AstConstExp();
            newConstExp.addExp = evaluator.buildAddExp(evaluator.evaluate(constInitVal.constExp));
            constInitVal.constExp = newConstExp;

            symConstVar.values.add(
                    evaluator.evaluate(constInitVal.constExp));
        } else if (constInitVal.stringConst == null) {
            ArrayList<AstConstExp> newConstExps = new ArrayList<>();
            for (AstConstExp constExp : constInitVal.constExps) {
                vConstExp(constExp);

                // Try folding this.
                AstConstExp newConstExp = new AstConstExp();
                newConstExp.addExp = evaluator.buildAddExp(evaluator.evaluate(constExp));
                newConstExps.add(newConstExp);

                symConstVar.values.add(
                        evaluator.evaluate(constExp));
            }
            // Substitute original constExps.
            constInitVal.constExps.clear();
            constInitVal.constExps.addAll(newConstExps);
        } else { // StringConst
            symConstVar.values.addAll(
                    getStringConstPieces(constInitVal.stringConst.val.string));
        }
    }

    private ArrayList<Character> getStringConstPieces(String str) {
        ArrayList<Character> pieces = new ArrayList<>();

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\\') {
                char next = str.charAt(i + 1);
                assert Arrays.asList('a', 'b', 't', 'n', 'v', 'f', '\"', '\'', '\\', '0')
                        .contains(next);
                Character c = (char) switch (next) {
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
                };
                i++;
                pieces.add(c);
            } else {
                pieces.add(ch);
            }
        }

        // The "\0".
        pieces.add('\0');

        return pieces;
    }


    private void vVarDecl(AstVarDecl varDecl) throws IOException {
        for (AstVarDef varDef : varDecl.varDefs)
            vVarDef(varDef);
    }

    private void vVarDef(AstVarDef varDef) throws IOException {
        symTbl.addSymbol(Symbol.from(varDef));
        if (varDef.constExp != null) {
            vConstExp(varDef.constExp);
            // Try folding this.
            AstConstExp newConstExp = new AstConstExp();
            newConstExp.addExp = evaluator.buildAddExp(evaluator.evaluate(varDef.constExp));
            varDef.constExp = newConstExp;
        }
        if (varDef.initVal != null)
            vInitVal(varDef.initVal);
    }

    private void vInitVal(AstInitVal initVal) throws IOException {
        if (initVal.exp != null) {
            vExp(initVal.exp);
            // Try folding this.
            AstExp newExp = new AstExp();
            newExp.addExp = evaluator.buildAddExp(evaluator.evaluate(initVal.exp));
            if (newExp.addExp != null) {
                initVal.exp = newExp;
            }
        } else {
            ArrayList<AstExp> newExps = new ArrayList<>();
            for (AstExp exp : initVal.exps) {
                vExp(exp);
                // Try folding this.
                AstExp newExp = new AstExp();
                newExp.addExp = evaluator.buildAddExp(evaluator.evaluate(exp));
                if (newExp.addExp != null) {
                    newExps.add(newExp);
                } else {
                    newExps.add(exp);
                }
            }
            initVal.exps.clear();
            initVal.exps.addAll(newExps);
        }
    }

    private void vStmt(AstStmt stmt, boolean enterLoop) throws IOException {
        if (stmt instanceof AstStmtAssign s) {
            vLVal(s.lVal); // Undefined error eliminated here.
            vExp(s.exp);
            // Try folding this.
            AstExp newExp = new AstExp();
            newExp.addExp = evaluator.buildAddExp(evaluator.evaluate(s.exp));
            if (newExp.addExp != null) {
                s.exp = newExp;
            }
            if (symTbl.searchSym(s.lVal.ident) instanceof SymConstVar)
                loggerErr.error(s.lVal.ident.lineNo, "h");

        } else if (stmt instanceof AstStmtSingleExp s) {
            if (s.exp != null) {
                vExp(s.exp);
                // Try folding this.
                AstExp newExp = new AstExp();
                newExp.addExp = evaluator.buildAddExp(evaluator.evaluate(s.exp));
                if (newExp.addExp != null) {
                    s.exp = newExp;
                }
            }

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
                // Try folding this.
                AstExp newExp = new AstExp();
                newExp.addExp = evaluator.buildAddExp(evaluator.evaluate(s.firstForStmt.exp));
                if (newExp.addExp != null) {
                    s.firstForStmt.exp = newExp;
                }
                if (symTbl.searchSym(s.firstForStmt.lVal.ident) instanceof SymConstVar)
                    loggerErr.error(s.firstForStmt.lVal.ident.lineNo, "h");
            }
            if (s.cond != null)
                vCond(s.cond);
            if (s.thirdForStmt != null) {
                vLVal(s.thirdForStmt.lVal);
                vExp(s.thirdForStmt.exp);
                // Try folding this.
                AstExp newExp = new AstExp();
                newExp.addExp = evaluator.buildAddExp(evaluator.evaluate(s.thirdForStmt.exp));
                if (newExp.addExp != null) {
                    s.thirdForStmt.exp = newExp;
                }
                if (symTbl.searchSym(s.thirdForStmt.lVal.ident) instanceof SymConstVar)
                    loggerErr.error(s.thirdForStmt.lVal.ident.lineNo, "h");
            }
            vStmt(s.stmt, true);

        } else if (stmt instanceof AstStmtBreak s) {
            if (!symTbl.inLoop() && !enterLoop) {
                loggerErr.error(s.token.lineNo, "m");
            }

        } else if (stmt instanceof AstStmtContinue s) {
            if (!symTbl.inLoop() && !enterLoop) {
                loggerErr.error(s.token.lineNo, "m");
            }

        } else if (stmt instanceof AstStmtReturn s) {
            if (s.exp != null && symTbl.curFuncEnv() == SymId.VoidFunc) {
                loggerErr.error(s.returnTk.lineNo, "f");
            }
            if (s.exp != null) {
                vExp(s.exp);
                // Try folding this.
                AstExp newExp = new AstExp();
                newExp.addExp = evaluator.buildAddExp(evaluator.evaluate(s.exp));
                if (newExp.addExp != null) {
                    s.exp = newExp;
                }
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

        ArrayList<AstExp> newExps = new ArrayList<>();
        for (AstExp exp : stmtPrintf.exps) {
            vExp(exp);
            // Try folding this.
            AstExp newExp = new AstExp();
            newExp.addExp = evaluator.buildAddExp(evaluator.evaluate(exp));
            if (newExp.addExp != null) {
                newExps.add(newExp);
            } else {
                newExps.add(exp);
            }
        }
        stmtPrintf.exps.clear();
        stmtPrintf.exps.addAll(newExps);
    }

    private void vCond(AstCond cond) throws IOException {
        vLOrExp(cond.lOrExp);
    }

    private void vConstExp(AstConstExp constExp) throws IOException {
        vAddExp(constExp.addExp);
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
                final ArrayList<SymId> fParams = sym.paramTypes;
                if (s.funcRParams == null) {
                    if (!fParams.isEmpty()) {
                        loggerErr.error(s.funcIdent.lineNo, "d");
                    }
                    return;
                }

                final ArrayList<AstExp> rParams = s.funcRParams.exps;
                ArrayList<AstExp> newExps = new ArrayList<>();
                for (AstExp exp : rParams) {
                    vExp(exp);

                    // Try folding this.
                    AstExp newExp = new AstExp();
                    newExp.addExp = evaluator.buildAddExp(evaluator.evaluate(exp));
                    if (newExp.addExp != null) {
                        newExps.add(newExp);
                    } else {
                        newExps.add(exp);
                    }
                }
                rParams.clear();
                rParams.addAll(newExps);

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
            // Try folding this.
            AstExp newExp = new AstExp();
            newExp.addExp = evaluator.buildAddExp(evaluator.evaluate(primaryExp.bracedExp));
            if (newExp.addExp != null) {
                primaryExp.bracedExp= newExp;
            }
        } else if (primaryExp.lVal != null) {
            vLVal(primaryExp.lVal);
        }
        primaryExp.type = typer.typeof(primaryExp);
    }

    private void vLVal(AstLVal lVal) throws IOException {
        vIdent(lVal.ident, false);
        if (lVal.exp != null) {
            vExp(lVal.exp);
            // Try folding this.
            AstExp newExp = new AstExp();
            newExp.addExp = evaluator.buildAddExp(evaluator.evaluate(lVal.exp));
            if (newExp.addExp != null) {
                lVal.exp = newExp;
            }
        }
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

    private class Evaluator {
        // Constant expanding helper.
        private Object evaluate(AstConstExp constExp) {
            Object res = evaluate(constExp.addExp);
            assert res != null;
            return res;
        }

        private Object evaluate(AstExp exp) {
            return evaluate(exp.addExp);
        }

        private Object evaluate(AstAddExp addExp) {
            Object firstElm = evaluate(addExp.mulExps.get(0));
            // Single sub-exp.
            if (addExp.operators.isEmpty()) {
                return firstElm;
            }

            // Multiple sub-exp, must be integer.
            Integer val = 0;

            // Init with first element.
            if (firstElm == null) {
                return null;
            } else if (firstElm instanceof Integer firstInt) {
                val = firstInt;
            } else if (firstElm instanceof Character firstChar) {
                val = Integer.valueOf(firstChar);
            }

            // Traverse through remaining all elements.
            for (int i = 1; i < addExp.mulExps.size(); i++) {
                Object elm = evaluate(addExp.mulExps.get(i));
                int op = addExp.operators.get(i - 1) == Token.TokenId.PLUS ? 1 : -1;
                if (elm == null) {
                    return null;
                } else if (elm instanceof Integer integer) {
                    val += op * integer;
                } else if (elm instanceof Character character) {
                    val += op * Integer.valueOf(character);
                }
            }

            return val;
        }

        private Object evaluate(AstMulExp mulExp) {
            Object firstElm = evaluate(mulExp.unaryExps.get(0));
            // Single sub-exp.
            if (mulExp.operators.isEmpty()) {
                return firstElm;
            }

            // Multiple sub-exp, must be integer.
            Integer val = 0;

            // Init with first element.
            if (firstElm == null) {
                return null;
            } else if (firstElm instanceof Integer firstInt) {
                val = firstInt;
            } else if (firstElm instanceof Character firstChar) {
                val = Integer.valueOf(firstChar);
            }

            // Traverse through remaining all elements.
            for (int i = 1; i < mulExp.unaryExps.size(); i++) {
                Object elm = evaluate(mulExp.unaryExps.get(i));
                Token.TokenId op = mulExp.operators.get(i - 1);
                if (elm == null) {
                    return null;
                } else if (elm instanceof Integer integer) {
                    val = switch (op) {
                        case MULT -> val * integer;
                        case DIV ->  val / integer;
                        case MOD ->  val % integer;
                        default ->   0;
                    };
                } else if (elm instanceof Character character) {
                    val = switch (op) {
                        case MULT -> val * Integer.valueOf(character);
                        case DIV ->  val / Integer.valueOf(character);
                        case MOD ->  val % Integer.valueOf(character);
                        default ->   0;
                    };
                }
            }

            return val;
        }

        private Object evaluate(AstUnaryExp unaryExp) {
            if (unaryExp instanceof AstUnaryExpFuncCall) {
                return null;
            } else if (unaryExp instanceof AstUnaryExpUnaryOp unaryExpUnaryOp) {
                if (unaryExpUnaryOp.unaryOp.op.tokenId == Token.TokenId.PLUS) {
                    return evaluate(unaryExpUnaryOp.unaryExp);
                } else { // -
                    Object valUnaryExp = evaluate(unaryExpUnaryOp.unaryExp);
                    if (valUnaryExp == null) {
                        return null;
                    } else if (valUnaryExp instanceof Character charUnaryExp) {
                        return -Integer.valueOf(charUnaryExp);
                    } else {
                        return -(Integer) valUnaryExp;
                    }
                }
            } else { // PrimaryExp
                return evaluate(((AstUnaryExpPrimary) unaryExp).primaryExp);
            }
        }

        private Object evaluate(AstPrimaryExp primaryExp) {
            if (primaryExp.bracedExp != null) {
                return evaluate(primaryExp.bracedExp);
            } else if (primaryExp.number != null) {
                return primaryExp.number.intConst.val.integer;
            } else if (primaryExp.character != null) {
                return primaryExp.character.charConst.val.character;
            } else {
                assert primaryExp.lVal != null;
                // Dealing with variables.
                Symbol symbol = symTbl.searchSym(primaryExp.lVal.ident);
                if (symbol instanceof SymVar) {
                    return null; // Vars cannot be evaluated at compile time!
                } else if (symbol instanceof SymConstVar constVar) {
                    if (!constVar.isArray) {
                        // Evaluate const single variable.
                        return constVar.values.get(0);
                    } else {
                        // Evaluate const (indexed) array.
                        Object indObj = evaluator.evaluate(primaryExp.lVal.exp); // No expand here, just evaluate.
                        int ind = indObj instanceof Integer ? (Integer) indObj :
                                indObj instanceof Character ? Integer.valueOf((Character) indObj)
                                : -1;
                        return constVar.values.get(ind);
                    }
                } else {
                    assert false; // Function symbol cannot appear here.
                    return null;
                }
            }
        }

        private AstAddExp buildAddExp(Object value) {
            if (value == null) {
                return null;
            }

            assert value instanceof Integer || value instanceof Character;
            AstPrimaryExp primaryExp = new AstPrimaryExp();
            if (value instanceof Integer integer) {
                Token token = new Token(Token.TokenId.INTCON, integer.toString(), 0);
                AstNumber number = new AstNumber();
                number.intConst = token;
                primaryExp.number = number;
                primaryExp.type = ConstInt;
            } else {
                Character character = (Character) value;
                // TODO about escaping char?
                Token token = new Token(Token.TokenId.CHRCON,
                        "'" + character + "'", 0);
                AstCharacter astCharacter = new AstCharacter();
                astCharacter.charConst = token;
                primaryExp.character = astCharacter;
                primaryExp.type = ConstChar;
            }

            AstUnaryExpPrimary unaryExpPrimary = new AstUnaryExpPrimary();
            unaryExpPrimary.primaryExp = primaryExp;
            unaryExpPrimary.type = primaryExp.type;

            AstMulExp mulExp = new AstMulExp();
            mulExp.addUnaryExp(unaryExpPrimary);
            mulExp.type = unaryExpPrimary.type;

            AstAddExp addExp = new AstAddExp();
            addExp.addMulExp(mulExp);
            addExp.type = mulExp.type;

            return addExp;
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
            return typeof(constExp.addExp);
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
                        Int,
                        Char,
                        ConstInt,
                        ConstChar).contains(symId);
                return symId;
            } else if (unaryExp instanceof AstUnaryExpFuncCall p) {
                Symbol s = symTbl.searchSym(p.funcIdent);
                assert Arrays.asList(
                        IntFunc,
                        CharFunc,
                        VoidFunc).contains(s.symId);
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
