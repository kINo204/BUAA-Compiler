package frontend.datastruct.symtbl;

import frontend.datastruct.ast.Token;
import frontend.datastruct.symbol.Symbol;
import utils.Log;
import ir.datastruct.operand.Label;

import java.io.IOException;
import java.util.HashSet;

public class SymTbl {
    private final Scope scopesRoot;
    private final HashSet<Scope> scopes = new HashSet<>(); // index for all scopes
    private Scope currentScope;
    private int curId = 1;

    private final Log loggerErr;

    public SymTbl(Log e) {
        scopesRoot = new Scope(curId, null, false, null);
        scopes.add(scopesRoot);
        currentScope = scopesRoot;
        loggerErr = e;
    }

    public void initVisits() {
        currentScope = scopesRoot;
        for (Scope s : scopes) {
            s.subScopeVisitingIndex = 0;
        }
    }

    public Symbol searchSym(Token identToken) {
        String literal = identToken.literal;
        Scope seaching = currentScope;
        while (seaching != null) {
            Symbol res = seaching.searchSym(literal);
            if (res != null) {
                return res;
            } else {
                seaching = seaching.upperScope;
            }
        }
        return null;
    }

    public void addSymbol(Symbol s) throws IOException {
        // We ignore any redefined symbol at the time.
        if (currentScope.containsSym(s.literal)) {
            loggerErr.println(s.lineNo + " b");
        } else {
            currentScope.addSym(s);
        }
    }

    /*
    Our scope managing scenarios:
    - Plain block statement: { ... }
    - Function def: ( params ) Block
    - For stmt: ( for-stmt ) Stmt
    - If stmt: ( if-stmt ) Stmt
     */

    public void pushScope(boolean isLoop, Symbol.SymId enterFuncEnv) {
        Scope newScope = new Scope(++curId, currentScope,
                isLoop || currentScope.inLoop,
                enterFuncEnv == null ? currentScope.functionEnv : enterFuncEnv
                );
        currentScope.subScopes.add(newScope);
        scopes.add(newScope);
        // Enter that new scope:
        currentScope = newScope;
    }

    public void setLoopLabels(Label forMotion, Label forEnd) {
        currentScope.setLoopLabels(forMotion, forEnd, true);
    }

    public Label getCurLoopMotion() {
        return currentScope.forMotion;
    }

    public Label getCurLoopEnd() {
        return currentScope.forEnd;
    }

    public void enterScope() {
        currentScope = currentScope.subScopes.get(
                currentScope.subScopeVisitingIndex++);
    }

    // Used both in visit and editing.
    public void exitScope() {
        currentScope = currentScope.upperScope;
    }

    public boolean inLoop() {
        return currentScope.inLoop;
    }

    public Symbol.SymId curFuncEnv() {
        return currentScope.functionEnv;
    }

    @Override
    public String toString() {
        return scopesRoot.toString();
    }
}
