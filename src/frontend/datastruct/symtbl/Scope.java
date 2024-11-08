package frontend.datastruct.symtbl;

import frontend.datastruct.symbol.Symbol;
import ir.datastruct.operand.Label;

import java.util.ArrayList;
import java.util.HashMap;

class Scope {
    private final int id;

    final Scope upperScope;
    final ArrayList<Scope> subScopes = new ArrayList<>();

    final boolean inLoop;
    Label forMotion;
    Label forEnd;
    final Symbol.SymId functionEnv;

    int subScopeVisitingIndex = 0;
    private final HashMap<String, Symbol> symbolMap = new HashMap<>();
    private final ArrayList<Symbol> symbolArray = new ArrayList<>();

    Scope(int id, Scope upperScope, boolean inLoop, Symbol.SymId functionEnv) {
        this.id = id;
        this.upperScope = upperScope;
        this.inLoop = inLoop;
        this.functionEnv = functionEnv;
    }

    boolean containsSym(String literal) {
        return symbolMap.containsKey(literal);
    }

    Symbol searchSym(String literal) {
        return symbolMap.get(literal);
    }

    void addSym(Symbol symbol) {
        symbolMap.put(symbol.literal, symbol);
        symbolArray.add(symbol);
        symbol.symtblId = this.id;
    }

    void setLoopLabels(Label forMotion, Label forEnd, boolean rootCall) {
        if (!rootCall) {
            this.forMotion = forMotion;
            this.forEnd = forEnd;
        }
        for (Scope scope : subScopes) {
            scope.setLoopLabels(forMotion, forEnd, false);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Symbol s : symbolArray) {
            str.append(id).append(" ");
            str.append(s).append("\n");
        }
        for (Scope scope : subScopes) {
            str.append(scope.toString());
        }
        return str.toString();
    }
}
