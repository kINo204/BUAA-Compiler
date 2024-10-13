package datastruct.symtbl;

import datastruct.ast.Token;
import datastruct.symbol.Symbol;

import java.util.ArrayList;
import java.util.HashMap;

class Scope {
    private final int id;

    final Scope upperScope;
    final ArrayList<Scope> subScopes = new ArrayList<>();

    final boolean inLoop;
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
