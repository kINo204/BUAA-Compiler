package frontend;

import java.math.BigInteger;

public class Token {
    private final TokenId tokenId;
    private final String literal; // Original token string.
    public Value val = new Value();

    public static class Value {
        public int integer;
        public BigInteger bigInteger;
        public char character;
        public String string;
    }

    public Token(Token.TokenId tokenId, String literal) {
        this.tokenId = tokenId;
        this.literal = literal;
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
            case CHRCON -> val.character = literal.charAt(0);
            case STRCON -> val.string = literal.substring(1, literal.length() - 1); // Strip "" symbols
        }
    }

    @Override
    public String toString() {
        return tokenId.toString() + " " + literal;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public enum TokenId {
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
