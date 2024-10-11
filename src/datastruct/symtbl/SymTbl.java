package datastruct.symtbl;

import datastruct.symbol.Symbol;
import io.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class SymTbl {
    private final Scope scopesRoot;
    private final HashSet<Scope> scopes = new HashSet<>(); // index for all scopes
    private Scope currentScope;
    private int curId = 1;

    private final Log loggerOut;
    private final Log loggerErr;

    public SymTbl(Log o, Log e) {
        scopesRoot = new Scope(curId, null);
        scopes.add(scopesRoot);
        currentScope = scopesRoot;
        loggerOut = o;
        loggerErr = e;
    }

    public void initVisits() {
        currentScope = scopesRoot;
        for (Scope s : scopes) {
            s.subScopeIndex = 0;
        }
    }

    public Symbol searchSym(String literal) {
        Scope seaching = currentScope;
        while (seaching != null) {
            if (seaching.symbolMap.containsKey(literal)) {
                return seaching.symbolMap.get(literal);
            } else {
                seaching = seaching.upperScope;
            }
        }
        return null;
    }

    public void addSyms(ArrayList<Symbol> symbols) throws IOException {
        for (Symbol s : symbols) {
            // We ignore any redefined symbol at the time.
            if (currentScope.symbolMap.containsKey(s.literal)) {
                loggerErr.println(s.lineNo + " b");
            } else {
                currentScope.symbolMap.put(s.literal, s);
                currentScope.symbolArray.add(s);
            }
        }
    }

    /*
    Our scope managing scenarios:
    - Plain block statement: { ... }
    - Function def: ( params ) Block
    - For stmt: ( for-stmt ) Stmt
    - If stmt: ( if-stmt ) Stmt
     */

    public void pushScope() {
        Scope newScope = new Scope(++curId, currentScope);
        currentScope.subScopes.add(newScope);
        scopes.add(newScope);
        // Enter that new scope:
        currentScope = newScope;
    }

    public void enterScope() {
        currentScope = currentScope.subScopes.get(
                currentScope.subScopeIndex++);
    }

    // Used both in visit and editing.
    public void exitScope() {
        currentScope = currentScope.upperScope;
    }

    @Override
    public String toString() {
        return scopesRoot.toString();
    }
}
