package frontend.datastruct.ast;

import java.math.BigInteger;
import java.util.Objects;

public class Token {
    public final int lineNo;
    public final TokenId tokenId;
    public final String literal; // Original token string.
    public final Value val = new Value();

    public static class Value {
        public int integer;
        public BigInteger bigInteger;
        public char character;
        public String string;
    }

    public Token(Token.TokenId tokenId, String literal, int lineNo) {
        this.tokenId = tokenId;
        this.literal = literal;
        this.lineNo = lineNo;
        parse();
    }

    private void parse() {
        switch (tokenId) {
            case INTCON -> {
                try {
                    val.integer = Integer.parseInt(literal);
                } catch (NumberFormatException e) {
                    val.bigInteger = new BigInteger(literal);
                }
            }
            case CHRCON -> val.character = literal.charAt(1) == '\\' ? (char) switch(literal.charAt(2)) {
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
                default -> throw new IllegalStateException("Unexpected escaping char in chrcon");
            } : literal.charAt(1);
            case STRCON -> val.string = literal.substring(1, literal.length() - 1); // Strip "" symbols
        }
    }

    public TokenId getTokenId() {
        return tokenId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token token)) return false;
        return tokenId == token.tokenId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenId);
    }

    public String output() {
        return tokenId.toString() + " " + literal;
    }

    @Override
    public String toString() {
        return literal;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public enum TokenId {
        EOF,

        // Literals:
        IDENFR, // identifier
        INTCON, // integer const
        STRCON, // string const
        CHRCON, // char const

        // Keywords(*_TK):
        MAINTK,
        CONSTTK,
        VOIDTK,
        INTTK,
        CHARTK,
        BREAKTK,
        CONTINUETK,
        IFTK,
        ELSETK,
        FORTK,
        GETINTTK,
        GETCHARTK,
        PRINTFTK,
        RETURNTK,

        // Operators:
        NOT,
        AND,
        OR,
        PLUS,
        MINU,
        MULT,
        DIV,
        MOD,
        LSS, // <
        LEQ, // <=
        GRE, // >
        GEQ, // >=
        EQL, // ==
        NEQ, // !=
        ASSIGN,
        // Syntax flag chars:
        SEMICN,
        COMMA,
        LPARENT,
        RPARENT,
        LBRACK,
        RBRACK,
        LBRACE,
        RBRACE
    }
}
