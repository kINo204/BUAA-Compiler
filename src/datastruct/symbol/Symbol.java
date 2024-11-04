package datastruct.symbol;

import datastruct.ast.*;

import java.util.Objects;

public abstract class Symbol {
    public final SymId symId;
    public int symtblId;
    public String literal;
    public final int lineNo;

    public Symbol(SymId symId, Token ident) {
        assert ident.getTokenId() == Token.TokenId.IDENFR;
        this.literal = ident.literal;
        this.symId = symId;
        this.lineNo = ident.lineNo;
    }

    public static Symbol from(AstConstDef constDef) {
        return new SymConstVar(constDef);
    }

    public static Symbol from(AstVarDef varDef) {
        return new SymVar(varDef);
    }

    public static Symbol from(AstFuncDef funcDef) {
        return new SymFunc(funcDef);
    }

    public static Symbol from(AstFuncFParam param) {
        return new SymVar(param);
    }

    @Override
    public String toString() {
        return literal + " " + symId.toString() ;
    }

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
