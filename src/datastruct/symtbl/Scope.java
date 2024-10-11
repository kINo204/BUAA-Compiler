package datastruct.symtbl;

import datastruct.symbol.Symbol;

import java.util.ArrayList;
import java.util.HashMap;

public class Scope {
    private final int id;
    final Scope upperScope;
    final ArrayList<Scope> subScopes = new ArrayList<>();
    int subScopeIndex = 0;
    final HashMap<String, Symbol> symbolMap = new HashMap<>();
    final ArrayList<Symbol> symbolArray = new ArrayList<>();

    Scope(int id, Scope upperScope) {
        this.id = id;
        this.upperScope = upperScope;
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
