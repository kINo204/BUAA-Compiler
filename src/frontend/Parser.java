package frontend;

import datastruct.ast.Token;
import datastruct.ast.*;
import io.Log;

import java.io.CharConversionException;
import java.io.IOException;
import java.util.Arrays;

import static datastruct.ast.Token.TokenId.*;

public class Parser {
    private final Lexer lexer;
    private final AstNode.AstNodeId topLevel;
    private final Log loggerOut;
    private final Log loggerErr;

    public Parser(Lexer lexer, String topLevel, Log o, Log e) {
        this.lexer = lexer;
        this.topLevel = AstNode.AstNodeId.valueOf(topLevel);
        loggerOut = o;
        loggerErr = e;
    }

    // Helpers.
    /** Step the expected token id, or report error and maintain position. */
    private void expectTokenId(Token.TokenId tokenId, String errCode) throws IOException {
        if (lexer.lookAhead(0).getTokenId() == tokenId) {
            lexer.read();
        } else {
            loggerErr.error(lexer.getCurrentLine(), errCode);
        }
    }

    // Parsing entry point.
    public AstNode parse() throws IOException {
        return switch (topLevel) {
            case CompUnit -> parseCompUnit();
            case FuncDef -> parseFuncDef();
            case FuncType -> parseFuncType();
            case FuncFParams -> parseFuncFParams();
            case FuncFParam -> parseFuncFParam();
            case MainFuncDef -> parseMainFuncDef();
            case Block -> parseBlock();
            case BlockItem -> parseBlockItem();
            case Decl -> parseDecl();
            case ConstDecl -> parseConstDecl();
            case ConstDef -> parseConstDef(null);
            case ConstInitVal -> parseConstInitVal();
            case VarDecl -> parseVarDecl();
            case VarDef -> parseVarDef(null);
            case InitVal -> parseInitVal();
            case Stmt -> parseStmt();
            case ForStmt -> parseForStmt();
            case Cond -> parseCond();
            case Exp -> parseExp();
            case ConstExp -> parseConstExp();
            case LOrExp -> parseLOrExp();
            case LAndExp -> parseLAndExp();
            case EqExp -> parseEqExp();
            case RelExp -> parseRelExp();
            case AddExp -> parseAddExp();
            case MulExp -> parseMulExp();
            case UnaryExp -> parseUnaryExp();
            case UnaryOp -> parseUnaryOp();
            case FuncRParams -> parseFuncRParams();
            case PrimaryExp -> parsePrimaryExp();
            case LVal -> parseLVal();
            case Number -> parseNumber();
            case Character -> parseCharacter();
        };
    }

    // Each parse*() will return an AstNode, the root of the subtree.
    private AstCompUnit parseCompUnit() throws IOException {
        AstCompUnit compUnit = new AstCompUnit();

        // { Decl }
        while (lexer.lookAhead(1).getTokenId() != MAINTK
                && lexer.lookAhead(2).getTokenId() != LPARENT) {
            compUnit.addDecl(parseDecl());
        }
        // { FuncDef }
        while (lexer.lookAhead(1).getTokenId() != MAINTK) {
            compUnit.addFuncDef(parseFuncDef());
        }
        // MainFuncDef
        compUnit.setMainFuncDef(parseMainFuncDef());

        loggerOut.println(compUnit.output());
        return compUnit;
    }

    private AstFuncDef parseFuncDef() throws IOException { // j
        AstFuncDef funcDef = new AstFuncDef();

        // FuncType
        funcDef.setFuncType(parseFuncType());
        // Ident
        funcDef.setIdent(lexer.read());
        // '('
        assert lexer.lookAhead(0).getTokenId() == LPARENT;
		lexer.read();
        // [ FuncFParams ]
        Token.TokenId t = lexer.lookAhead(0).getTokenId();
        if (t == CHARTK || t == INTTK) {
            funcDef.setFuncFParams(parseFuncFParams());
        }
        // ')'
        expectTokenId(RPARENT, "j");
        // Block
        funcDef.setBlock(parseBlock());

        loggerOut.println(funcDef.output());


        return funcDef;
    }

    private AstFuncType parseFuncType() throws IOException {
        AstFuncType funcType = new AstFuncType();

        // 'void' | BType
        funcType.setType(lexer.read());

        loggerOut.println(funcType.output());
        return funcType;
    }

    private AstFuncFParams parseFuncFParams() throws IOException {
        AstFuncFParams funcFParams = new AstFuncFParams();

        // FuncFParam
        funcFParams.addFuncFParam(parseFuncFParam());
        // { ',' FuncFParam }
        while (lexer.lookAhead(0).getTokenId() == COMMA) {
            lexer.read();
            funcFParams.addFuncFParam(parseFuncFParam());
        }

        loggerOut.println(funcFParams.output());
        return funcFParams;
    }

    private AstFuncFParam parseFuncFParam() throws IOException { // k
        AstFuncFParam funcFParam = new AstFuncFParam();

        // BType
        funcFParam.setType(lexer.read());
        // Ident
        funcFParam.setIdent(lexer.read());
        // [ '[' ']' ]
        if (lexer.lookAhead(0).getTokenId() == LBRACK) {
            lexer.read();
            funcFParam.setArray(true);
            expectTokenId(RBRACK, "k");
        } else {
            funcFParam.setArray(false);
        }

        loggerOut.println(funcFParam.output());
        return funcFParam;
    }

    private AstMainFuncDef parseMainFuncDef() throws IOException { // j
        AstMainFuncDef mainFuncDef = new AstMainFuncDef();

        // 'int'
        assert lexer.lookAhead(0).getTokenId() == INTTK;
		lexer.read();
        // 'main'
        assert lexer.lookAhead(0).getTokenId() == MAINTK;
		lexer.read();
        // '(' ')'
        assert lexer.lookAhead(0).getTokenId() == LPARENT;
		lexer.read();
        expectTokenId(RPARENT, "j");
        // Block
        mainFuncDef.setBlock(parseBlock());

        loggerOut.println(mainFuncDef.output());
        return mainFuncDef;
    }

    private AstBlock parseBlock() throws IOException {
        AstBlock block = new AstBlock();

        // '{'
        assert lexer.lookAhead(0).getTokenId() == LBRACE;
		lexer.read();
        // { BlockItem }
        while (lexer.lookAhead(0).getTokenId() != RBRACE) {
            block.addBlockItem(parseBlockItem());
        }
        // '}'
        assert lexer.lookAhead(0).getTokenId() == RBRACE;
		block.braceEnd = lexer.read();

        loggerOut.println(block.output());

        return block;
    }

    private AstBlockItem parseBlockItem() throws IOException {
        AstBlockItem blockItem = new AstBlockItem();
        Token.TokenId t = lexer.lookAhead(0).getTokenId();
        if (Arrays.asList(CONSTTK, INTTK, CHARTK).contains(t)) {
            // Decl
            blockItem.setContent(parseDecl());
        } else {
            // Stmt
            blockItem.setContent(parseStmt());
        }

        // loggerOut.println(blockItem.output());
        return blockItem;
    }

    private AstDecl parseDecl() throws IOException {
        AstDecl decl = new AstDecl();

        if (lexer.lookAhead(0).getTokenId() == CONSTTK) {
            decl.setContent(parseConstDecl());
        } else {
            decl.setContent(parseVarDecl());
        }

        // loggerOut.println(decl.output());
        return decl;
    }

    private AstConstDecl parseConstDecl() throws IOException { // i
        AstConstDecl constDecl = new AstConstDecl();

        // const
        assert lexer.lookAhead(0).getTokenId() == CONSTTK;
		lexer.read();
        // BType
        constDecl.setType(lexer.read());
        // ConstDef
        constDecl.addConstDef(parseConstDef(constDecl.type));
        // { , ConstDef }
        while (lexer.lookAhead(0).getTokenId() == COMMA) {
            lexer.read();
            constDecl.addConstDef(parseConstDef(constDecl.type));
        }
        // ;
        expectTokenId(SEMICN, "i");

        loggerOut.println(constDecl.output());
        return constDecl;
    }

    private AstConstDef parseConstDef(Token type) throws IOException { // k
        AstConstDef constDef = new AstConstDef();

        constDef.type = type;
        // Ident
        constDef.setIdent(lexer.read());
        // [ '[' ConstExp ']' ]
        if (lexer.lookAhead(0).getTokenId() == LBRACK) {
            lexer.read();
            constDef.setConstExp(parseConstExp());
            expectTokenId(RBRACK, "k");
        }
        // =
        assert lexer.lookAhead(0).getTokenId() == ASSIGN;
		lexer.read();
        // ConstInitVal
        constDef.setConstInitVal(parseConstInitVal());

        loggerOut.println(constDef.output());
        return constDef;
    }

    private AstConstInitVal parseConstInitVal() throws IOException {
        AstConstInitVal constInitVal = new AstConstInitVal();

        Token.TokenId t = lexer.lookAhead(0).getTokenId();
        if (t == LBRACE) { // '{' [ ConstExp, ... , ConstExp ] '}'
            lexer.read();
            if (lexer.lookAhead(0).getTokenId() != RBRACE) {
                constInitVal.addConstExp(parseConstExp());
                while (lexer.lookAhead(0).getTokenId() != RBRACE) {
                    lexer.read();
                    constInitVal.addConstExp(parseConstExp());
                }
            }
            lexer.read();
        } else if (t == STRCON) {
            constInitVal.setStringConst(lexer.read());
        } else { // ConstExp
            constInitVal.setConstExp(parseConstExp());
        }

        loggerOut.println(constInitVal.output());
        return constInitVal;
    }

    private AstVarDecl parseVarDecl() throws IOException { // i
        AstVarDecl varDecl = new AstVarDecl();

        varDecl.setType(lexer.read());
        varDecl.addVarDef(parseVarDef(varDecl.type));
        while (lexer.lookAhead(0).getTokenId() == COMMA) {
            lexer.read();
            varDecl.addVarDef(parseVarDef(varDecl.type));
        }
        expectTokenId(SEMICN, "i");

        loggerOut.println(varDecl.output());
        return varDecl;
    }

    private AstVarDef parseVarDef(Token type) throws IOException { // k
        AstVarDef varDef = new AstVarDef();

        varDef.type = type;
        varDef.setIdent(lexer.read());
        if (lexer.lookAhead(0).getTokenId() == LBRACK) {
            lexer.read();
            varDef.setConstExp(parseConstExp());
            expectTokenId(RBRACK, "k");
        }
        if (lexer.lookAhead(0).getTokenId() == ASSIGN) {
            lexer.read();
            varDef.setInitVal(parseInitVal());
        }

        loggerOut.println(varDef.output());
        return varDef;
    }

    private AstInitVal parseInitVal() throws IOException {
        AstInitVal initVal = new AstInitVal();

        Token.TokenId t = lexer.lookAhead(0).getTokenId();
        if (t == LBRACE) {
            lexer.read();
            if (lexer.lookAhead(0).getTokenId() != RBRACE) {
                initVal.addExp(parseExp());
                while (lexer.lookAhead(0).getTokenId() != RBRACE) {
                    lexer.read();
                    initVal.addExp(parseExp());
                }
            }
            lexer.read();
        } else if (t == STRCON) {
            initVal.setStringConst(lexer.read());
        } else { // ConstExp
            initVal.setExp(parseExp());
        }

        loggerOut.println(initVal.output());
        return initVal;
    }

    private AstStmt parseStmt() throws IOException { // various errors
        AstStmt stmt = switch (lexer.lookAhead(0).getTokenId()) {
            case LBRACE -> parseStmtBlock();
            case IFTK -> parseStmtIf();
            case FORTK -> parseStmtFor();
            case BREAKTK -> parseStmtBreak();
            case CONTINUETK -> parseStmtContinue();
            case RETURNTK -> parseStmtReturn();
            case PRINTFTK -> parseStmtPrintf();
            case SEMICN -> parseStmtSingleExp(); // with null Exp
            default -> null;
        };
        if (stmt != null) {
            return stmt;
        }

        // Look ahead until the ';'.
        int ind = 0;
        boolean hasAssignTk = false;
        boolean hasGetintTk = false;
        boolean hasGetchrTk = false;
        Token.TokenId t = lexer.lookAhead(ind).getTokenId();
        while (t != SEMICN) {
            if (t == ASSIGN) {
                hasAssignTk = true;
            }
            if (t == GETINTTK) {
                hasGetintTk = true;
                break;
            }
            if (t == GETCHARTK) {
                hasGetchrTk = true;
                break;
            }
            ind += 1;
            t = lexer.lookAhead(ind).getTokenId();
        }
        if (!hasAssignTk) { // SingleExp with not-null Exp
            stmt = parseStmtSingleExp();
        } else if (hasGetintTk) { // Getint
            stmt = parseStmtGetint();
        } else if (hasGetchrTk) { // Getchar
            stmt = parseStmtGetchar();
        } else { // Assign
            stmt = parseStmtAssign();
        }

        // loggerOut.println(stmt.output());
        return stmt;
    }

    private AstStmtAssign parseStmtAssign() throws IOException { // i
        AstStmtAssign stmtAssign = new AstStmtAssign();
        // LVal
        stmtAssign.setlVal(parseLVal());
        // =
        assert lexer.lookAhead(0).getTokenId() == ASSIGN;
		lexer.read();
        // Exp
        stmtAssign.setExp(parseExp());
        // ;
        expectTokenId(SEMICN, "i");

        loggerOut.println(stmtAssign.output());
        return stmtAssign;
    }

    private AstStmtSingleExp parseStmtSingleExp() throws IOException { // i
        AstStmtSingleExp stmtSingleExp = new AstStmtSingleExp();
        if (lexer.lookAhead(0).getTokenId() != SEMICN) {
            stmtSingleExp.setExp(parseExp());
            expectTokenId(SEMICN, "i");
        } else {
            assert lexer.lookAhead(0).getTokenId() == SEMICN;
            lexer.read(); // TODO expect?
        }

        loggerOut.println(stmtSingleExp.output());
        return stmtSingleExp;
    }

    private AstStmtBlock parseStmtBlock() throws IOException {
        AstStmtBlock stmtBlock = new AstStmtBlock();
        stmtBlock.setBlock(parseBlock());
        loggerOut.println(stmtBlock.output());
        return stmtBlock;
    }

    private AstStmtIf parseStmtIf() throws IOException { // j
        AstStmtIf stmtIf = new AstStmtIf();

        assert lexer.lookAhead(0).getTokenId() == IFTK;
		lexer.read();

        assert lexer.lookAhead(0).getTokenId() == LPARENT;
		lexer.read();

        stmtIf.setCond(parseCond());

        expectTokenId(RPARENT, "j");

        stmtIf.setIfStmt(parseStmt());
        if (lexer.lookAhead(0).getTokenId() == ELSETK) {
            lexer.read();
            stmtIf.setElseStmt(parseStmt());
        }

        loggerOut.println(stmtIf.output());
        return stmtIf;
    }

    private AstStmtFor parseStmtFor() throws IOException {
        AstStmtFor stmtFor = new AstStmtFor();
        assert lexer.lookAhead(0).getTokenId() == FORTK;
		lexer.read();
        assert lexer.lookAhead(0).getTokenId() == LPARENT;
		lexer.read();
        if (lexer.lookAhead(0).getTokenId() != SEMICN) {
            stmtFor.setFirstForStmt(parseForStmt());
        }
        assert lexer.lookAhead(0).getTokenId() == SEMICN;
		lexer.read();
        if (lexer.lookAhead(0).getTokenId() != SEMICN) {
            stmtFor.setCond(parseCond());
        }
        assert lexer.lookAhead(0).getTokenId() == SEMICN;
		lexer.read();
        if (lexer.lookAhead(0).getTokenId() != RPARENT) {
            stmtFor.setThirdForStmt(parseForStmt());
        }
        assert lexer.lookAhead(0).getTokenId() == RPARENT;
		lexer.read();
        stmtFor.setStmt(parseStmt());

        loggerOut.println(stmtFor.output());
        return stmtFor;
    }

    private AstStmtBreak parseStmtBreak() throws IOException { // i
        assert lexer.lookAhead(0).getTokenId() == BREAKTK;
        AstStmtBreak stmtBreak = new AstStmtBreak();
		stmtBreak.token = lexer.read();
        expectTokenId(SEMICN, "i");
        loggerOut.println(stmtBreak.output());
        return stmtBreak;
    }

    private AstStmtContinue parseStmtContinue() throws IOException { // i
        assert lexer.lookAhead(0).getTokenId() == CONTINUETK;
        AstStmtContinue stmtContinue = new AstStmtContinue();
		stmtContinue.token = lexer.read();
        expectTokenId(SEMICN, "i");
        loggerOut.println(stmtContinue.output());
        return stmtContinue;
    }

    private AstStmtReturn parseStmtReturn() throws IOException { // i
        AstStmtReturn stmtReturn = new AstStmtReturn();
        assert lexer.lookAhead(0).getTokenId() == RETURNTK;
		stmtReturn.returnTk = lexer.read();
        if (lexer.lookAhead(0).getTokenId() != SEMICN) {
            stmtReturn.setExp(parseExp());
        }
        expectTokenId(SEMICN, "i");
        loggerOut.println(stmtReturn.output());
        return stmtReturn;
    }

    private AstStmtGetint parseStmtGetint() throws IOException { // i, j
        AstStmtGetint stmtGetint = new AstStmtGetint();

        stmtGetint.setlVal(parseLVal());

        assert lexer.lookAhead(0).getTokenId() == ASSIGN;
		lexer.read();

        assert lexer.lookAhead(0).getTokenId() == GETINTTK;
		lexer.read();

        assert lexer.lookAhead(0).getTokenId() == LPARENT;
		lexer.read();

        expectTokenId(RPARENT, "j");
        expectTokenId(SEMICN, "i");
        loggerOut.println(stmtGetint.output());
        return stmtGetint;
    }

    private AstStmtGetchar parseStmtGetchar() throws IOException { // i, j
        AstStmtGetchar stmtGetchar = new AstStmtGetchar();
        stmtGetchar.setlVal(parseLVal());
        assert lexer.lookAhead(0).getTokenId() == ASSIGN;
		lexer.read();
        assert lexer.lookAhead(0).getTokenId() == GETCHARTK;
		lexer.read();
        assert lexer.lookAhead(0).getTokenId() == LPARENT;
		lexer.read();
        expectTokenId(RPARENT, "j");
        expectTokenId(SEMICN, "i");
        loggerOut.println(stmtGetchar.output());
        return stmtGetchar;
    }

    private AstStmtPrintf parseStmtPrintf() throws IOException { // i, j
        AstStmtPrintf stmtPrintf = new AstStmtPrintf();
        assert lexer.lookAhead(0).getTokenId() == PRINTFTK;
		stmtPrintf.printfTk = lexer.read();
        assert lexer.lookAhead(0).getTokenId() == LPARENT;
		lexer.read();
        stmtPrintf.setStringConst(lexer.read());
        while (lexer.lookAhead(0).getTokenId() == COMMA) {
            lexer.read();
            stmtPrintf.addExp(parseExp());
        }
        expectTokenId(RPARENT, "j");
        expectTokenId(SEMICN, "i");
        loggerOut.println(stmtPrintf.output());
        return stmtPrintf;
    }

    private AstForStmt parseForStmt() throws IOException {
        AstForStmt forStmt = new AstForStmt();
        forStmt.setlVal(parseLVal());
        assert lexer.lookAhead(0).getTokenId() == ASSIGN;
		lexer.read();
        forStmt.setExp(parseExp());
        loggerOut.println(forStmt.output());
        return forStmt;
    }

    private AstCond parseCond() throws IOException {
        AstCond cond = new AstCond();
        cond.setlOrExp(parseLOrExp());
        loggerOut.println(cond.output());
        return cond;
    }

    private AstExp parseExp() throws IOException {
        AstExp exp = new AstExp();
        exp.setAstAddExp(parseAddExp());
        loggerOut.println(exp.output());
        return exp;
    }

    private AstConstExp parseConstExp() throws IOException {
        AstConstExp constExp = new AstConstExp();
        constExp.setAstAddExp(parseAddExp());
        loggerOut.println(constExp.output());
        return constExp;
    }

    private AstLOrExp parseLOrExp() throws IOException {
        AstLOrExp lOrExp = new AstLOrExp();
        lOrExp.addLAndExp(parseLAndExp());
        while (lexer.lookAhead(0).getTokenId() == OR) {
            loggerOut.println(lOrExp.output());
            lexer.read();
            lOrExp.addLAndExp(parseLAndExp());
        }
        loggerOut.println(lOrExp.output());
        return lOrExp;
    }

    private AstLAndExp parseLAndExp() throws IOException {
        AstLAndExp lAndExp = new AstLAndExp();
        lAndExp.addEqExp(parseEqExp());
        while (lexer.lookAhead(0).getTokenId() == AND) {
            loggerOut.println(lAndExp.output());
            lexer.read();
            lAndExp.addEqExp(parseEqExp());
        }
        loggerOut.println(lAndExp.output());
        return lAndExp;
    }

    private AstEqExp parseEqExp() throws IOException {
        AstEqExp eqExp = new AstEqExp();
        eqExp.addRelExp(parseRelExp());
        while (Arrays.asList(EQL, NEQ)
                .contains(lexer.lookAhead(0).getTokenId())) {
            loggerOut.println(eqExp.output());
            lexer.read();
            eqExp.addRelExp(parseRelExp());
        }
        loggerOut.println(eqExp.output());
        return eqExp;
    }

    private AstRelExp parseRelExp() throws IOException {
        AstRelExp relExp = new AstRelExp();
        relExp.addAddExp(parseAddExp());
        while (Arrays.asList(GRE, GEQ, LSS, LEQ)
                .contains(lexer.lookAhead(0).getTokenId())) {
            loggerOut.println(relExp.output());
            lexer.read();
            relExp.addAddExp(parseAddExp());
        }
        loggerOut.println(relExp.output());
        return relExp;
    }

    private AstAddExp parseAddExp() throws IOException {
        AstAddExp addExp = new AstAddExp();
        addExp.addMulExp(parseMulExp());
        while (Arrays.asList(PLUS, MINU)
                .contains(lexer.lookAhead(0).getTokenId())) {
            loggerOut.println(addExp.output());
            lexer.read();
            addExp.addMulExp(parseMulExp());
        }
        loggerOut.println(addExp.output());
        return addExp;
    }

    private AstMulExp parseMulExp() throws IOException {
        AstMulExp mulExp = new AstMulExp();
        mulExp.addUnaryExp(parseUnaryExp());
        while (Arrays.asList(MULT, DIV, MOD)
                .contains(lexer.lookAhead(0).getTokenId())) {
            loggerOut.println(mulExp.output());
            lexer.read();
            mulExp.addUnaryExp(parseUnaryExp());
        }
        loggerOut.println(mulExp.output());
        return mulExp;
    }

    private AstUnaryExp parseUnaryExp() throws IOException { // j
        AstUnaryExp unaryExp;
        // UnaryOp UnaryExp
        if (Arrays.asList(PLUS, MINU, NOT)
                .contains(lexer.lookAhead(0).getTokenId())) {
            AstUnaryExpUnaryOp unaryExpUnaryOp = new AstUnaryExpUnaryOp();
            unaryExpUnaryOp.setUnaryOp(parseUnaryOp());
            unaryExpUnaryOp.setUnaryExp(parseUnaryExp());
            unaryExp = unaryExpUnaryOp;
        // FuncCall
        } else if (lexer.lookAhead(0).getTokenId() == IDENFR
                && lexer.lookAhead(1).getTokenId() == LPARENT) {
            AstUnaryExpFuncCall unaryExpFuncCall = new AstUnaryExpFuncCall();
            unaryExpFuncCall.setFuncIdent(lexer.read());

            assert lexer.lookAhead(0).getTokenId() == LPARENT;
            lexer.read();

            Token.TokenId t = lexer.lookAhead(0).getTokenId();
            if (Arrays.asList( // Search int FIRST(FuncRParams)
                    PLUS, MINU, NOT, // UnaryOp
                    IDENFR, // FuncCall, LVal
                    LPARENT, INTCON, CHRCON
                    )
                    .contains(t)) {
                unaryExpFuncCall.setFuncRParams(parseFuncRParams());
            }
            expectTokenId(RPARENT, "j");
            unaryExp = unaryExpFuncCall;
        } else { // PrimaryExp
            AstUnaryExpPrimary unaryExpPrimary = new AstUnaryExpPrimary();
            unaryExpPrimary.setPrimaryExp(parsePrimaryExp());
            unaryExp = unaryExpPrimary;
        }
        loggerOut.println(unaryExp.output());
        return unaryExp;
    }

    private AstUnaryOp parseUnaryOp() throws IOException {
        AstUnaryOp unaryOp = new AstUnaryOp();
        unaryOp.setOp(lexer.read());
        loggerOut.println(unaryOp.output());
        return unaryOp;
    }

    private AstFuncRParams parseFuncRParams() throws IOException {
        AstFuncRParams funcRParams = new AstFuncRParams();
        funcRParams.addExp(parseExp());
        while (lexer.lookAhead(0).getTokenId() == COMMA) {
            lexer.read();
            funcRParams.addExp(parseExp());
        }
        loggerOut.println(funcRParams.output());
        return funcRParams;
    }

    private AstPrimaryExp parsePrimaryExp() throws IOException { // j
        Token.TokenId t = lexer.lookAhead(0).getTokenId();
        AstPrimaryExp primaryExp = new AstPrimaryExp();
        switch (t) {
            case LPARENT:
                lexer.read();
                primaryExp.setBracedExp(parseExp());
                expectTokenId(RPARENT, "j");
                break;
            case IDENFR:
                primaryExp.setlVal(parseLVal());
                break;
            case INTCON:
                primaryExp.setNumber(parseNumber());
                break;
            case CHRCON:
                primaryExp.setCharacter(parseCharacter());
                break;
            default:
        }
        loggerOut.println(primaryExp.output());
        return primaryExp;
    }

    private AstLVal parseLVal() throws IOException { // k
        AstLVal lVal = new AstLVal();
        lVal.setIdent(lexer.read());
        if (lexer.lookAhead(0).getTokenId() == LBRACK) {
            lexer.read();
            lVal.setExp(parseExp());
            expectTokenId(RBRACK, "k");
        }
        loggerOut.println(lVal.output());
        return lVal;
    }

    private AstNumber parseNumber() throws IOException {
        AstNumber number = new AstNumber();
        number.setIntConst(lexer.read());
        loggerOut.println(number.output());
        return number;
    }

    private AstCharacter parseCharacter() throws IOException {
        AstCharacter character = new AstCharacter();
        character.setCharConst(lexer.read());
        loggerOut.println(character.output());
        return character;
    }
}