package datastruct.symbol;

import datastruct.ast.*;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Symbol {
    private final SymId symId;
    public String literal;
    public final int lineNo;

    /** Two symbols with the same literal are considered identical.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Symbol symbol)) return false;
        return Objects.equals(literal, symbol.literal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literal);
    }

    public Symbol(SymId symId, Token ident) {
        assert ident.getTokenId() == Token.TokenId.IDENFR;
        this.literal = ident.literal;
        this.symId = symId;
        this.lineNo = ident.lineNo;
    }

    public static ArrayList<Symbol> from(AstFuncDef n) {
        ArrayList<Symbol> symbols = new ArrayList<>();
        SymFunc func = new SymFunc(n);
        symbols.add(func);
        return symbols;
    }

    public static ArrayList<Symbol> from(AstVarDecl n) {
        ArrayList<Symbol> symbols = new ArrayList<>();
        Token.TokenId typePrefix = n.type.getTokenId();
        for (AstVarDef def: n.varDefs) {
            symbols.add(
                    new SymVar(typePrefix, def));
        }
        return symbols;
    }

    public static ArrayList<Symbol> from(AstConstDecl n) {
        ArrayList<Symbol> symbols = new ArrayList<>();
        Token.TokenId typePrefix = n.type.getTokenId();
        for (AstConstDef def: n.constDefs) {
            symbols.add(
                    new SymVar(typePrefix, def));
        }
        return symbols;
    }

    public static ArrayList<Symbol> from(AstFuncFParams n) {
        ArrayList<Symbol> symbols = new ArrayList<>();
        for (AstFuncFParam param : n.funcFParams) {
            symbols.add(new SymVar(param));
        }
        return symbols;
    }

    @Override
    public String toString() {
        return literal + " " + symId.toString() ;
    }

    public enum SymId {
        ConstChar,
        ConstInt,
        ConstCharArray,
        ConstIntArray,
        Char,
        Int,
        CharArray,
        IntArray,
        VoidFunc,
        CharFunc,
        IntFunc,
    }
}
