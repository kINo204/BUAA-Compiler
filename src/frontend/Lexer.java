package frontend;

import datastruct.ast.Token;
import io.Log;

import java.io.*;
import java.util.*;

import static datastruct.ast.Token.TokenId.*;


public class Lexer {
    @Override
    public String toString() {
        try {
            return "#LINE=" + currentLine + " NEXT=" + lookAhead(0).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Lexer(Reader reader, Log o, Log e) {
        // Init input.
        input = new BufferedReader(reader);
        if (!input.markSupported()) {
            System.err.println("Lexer marking not supported for this input stream!");
        }

        // Init output.
        loggerOut = o;
        loggerErr = e;
    }

    private int currentLine = 1;
    private int parsingLineNo = 1; // The line number the lexer's cursor is on.
    private final ArrayDeque<Token> readTokens = new ArrayDeque<>(); // LIFO, tokens already read in lookAhead.
    private final ArrayDeque<Integer> lineNos = new ArrayDeque<>();
    private final BufferedReader input;
    private final Log loggerOut;
    private final Log loggerErr;
    private static final HashMap<String, Token.TokenId> keywordsTbl = new HashMap<>();
    static {
        keywordsTbl.put("main", MAINTK);
        keywordsTbl.put("const", CONSTTK);
        keywordsTbl.put("void", VOIDTK);
        keywordsTbl.put("int", INTTK);
        keywordsTbl.put("char", CHARTK);
        keywordsTbl.put("break", BREAKTK);
        keywordsTbl.put("continue", CONTINUETK);
        keywordsTbl.put("if", IFTK);
        keywordsTbl.put("else", ELSETK);
        keywordsTbl.put("for", FORTK);
        keywordsTbl.put("getint", GETINTTK);
        keywordsTbl.put("getchar", GETCHARTK);
        keywordsTbl.put("printf", PRINTFTK);
        keywordsTbl.put("return", RETURNTK);
    }

    public int getCurrentLine() {
        return currentLine;
    }

    public Token read() throws IOException {
        Token t;
        if (!readTokens.isEmpty()) {
            t = readTokens.removeFirst();
            currentLine = lineNos.removeFirst();
        } else {
            t = parseToken();
            currentLine = parsingLineNo;
        }
        loggerOut.println(t);
        return t;
    }

    /**
     * Look ahead token by offset, without advancing the lexer's parsing process.
     * @param offset Token offset to look ahead. Use "0" to look at next token(same as read).
     * @return The token read. If EOF reached or exceeded, returns null.
     */
    public Token lookAhead(int offset) throws IOException {
        assert offset >= 0;
        int size = readTokens.size();
        // Token already buffered:
        if (offset < size) {
            int ind = 0;
            for (Token t : readTokens) {
                if (ind == offset) {
                    return t;
                }
                ind += 1;
            }
        }
        // Token not parsed yet:
        // If EOF has been reached, return.
        if (!readTokens.isEmpty() && readTokens.getLast().getTokenId() == EOF) {
            return null;
        }
        // Else continue parsing.
        for (int i = 0; i <= offset - size; i++) {
            readTokens.addLast(parseToken());
            lineNos.addLast(parsingLineNo);
            // If a null be added, we reach EOF. No need for parsing more tokens.
            if (readTokens.getLast().getTokenId() == EOF)
                break;
        }
        return readTokens.getLast();
    }

    // Returns null if EOF or token unidentified.
    private Token parseToken() throws IOException {
        Token token;
        char ch;
        // Get the next non-blank char.
        while (true) {
            // Jump blanks.
            do {
                int ich = input.read(); // Using integer to preserve EOF info.
                if (ich == -1)
                    return new Token(EOF, null, parsingLineNo); // EOF
                ch = (char) ich;
                if (ch == '\n')
                    parsingLineNo++;
            } while (String.valueOf(ch).isBlank());

            // Deal with comments.
            if (ch == '/') {
                input.mark(1);
                ch = (char) input.read();
                if (ch == '/') {
                    int ich;
                    do {
                        ich = input.read();
                        if (ich == '\n') {
                            parsingLineNo++;
                        }
                    } while (ich != '\n' && ich != -1);
                } else if (ch == '*') {
                    do {
                        ch = (char) input.read();
                        if (ch == '\n') {
                            parsingLineNo++;
                        }
                    } while (ch != '*');
                    ch = (char) input.read();
                    assert ch == '/';
                } else {
                    input.reset();
                    ch = '/';
                    break;
                }
                continue;
            }
            break;
        }

        // Predictable tokens by 1 char.
        token = switch (ch) {
            case ';' -> new Token(SEMICN, ";", parsingLineNo);
            case ',' -> new Token(COMMA, ",", parsingLineNo);
            case '(' -> new Token(LPARENT, "(", parsingLineNo);
            case ')' -> new Token(RPARENT, ")", parsingLineNo);
            case '[' -> new Token(LBRACK, "[", parsingLineNo);
            case ']' -> new Token(RBRACK, "]", parsingLineNo);
            case '{' -> new Token(LBRACE, "{", parsingLineNo);
            case '}' -> new Token(RBRACE, "}", parsingLineNo);
            case '+' -> new Token(PLUS, "+", parsingLineNo);
            case '-' -> new Token(MINU, "-", parsingLineNo);
            case '*' -> new Token(MULT, "*", parsingLineNo);
            case '/' -> new Token(DIV, "/", parsingLineNo);
            case '%' -> new Token(MOD, "%", parsingLineNo);
            default -> null;
        };
        if (token != null) {
            return token;
        }

        StringBuilder str = new StringBuilder(String.valueOf(ch));

        // Finite look-ahead tokens.
        switch (ch) {
            case '>':
                input.mark(1);
                str.append((char) input.read());
                if (str.toString().equals(">=")) {
                    token = new Token(GEQ, ">=", parsingLineNo);
                } else {
                    input.reset();
                    token = new Token(GRE, ">", parsingLineNo);
                }
                break;
            case '<':
                input.mark(1);
                str.append((char) input.read());
                if (str.toString().equals("<=")) {
                    token = new Token(LEQ, "<=", parsingLineNo);
                } else {
                    input.reset();
                    token = new Token(LSS, "<", parsingLineNo);
                }
                break;
            case '=':
                input.mark(1);
                str.append((char) input.read());
                if (str.toString().equals("==")) {
                    token = new Token(EQL, "==", parsingLineNo);
                } else {
                    input.reset();
                    token = new Token(ASSIGN, "=", parsingLineNo);
                }
                break;
            case '!':
                input.mark(1);
                str.append((char) input.read());
                if (str.toString().equals("!=")) {
                    token = new Token(NEQ, "!=", parsingLineNo);
                } else {
                    input.reset();
                    token = new Token(NOT, "!", parsingLineNo);
                }
                break;
            case '&':
                input.mark(1);
                str.append((char) input.read());
                if (str.toString().equals("&&")) {
                    token = new Token(AND, "&&", parsingLineNo);
                } else {
                    input.reset();
                    token = new Token(AND, "&&", parsingLineNo);
                    loggerErr.error(parsingLineNo, "a");
                }
                break;
            case '|':
                input.mark(1);
                str.append((char) input.read());
                if (str.toString().equals("||")) {
                    token = new Token(OR, "||", parsingLineNo);
                } else {
                    input.reset();
                    token = new Token(OR, "&&", parsingLineNo);
                    loggerErr.error(parsingLineNo, "a");
                }
                break;
        }
        if (token != null) {
            return token;
        }

        // Looping look-ahead tokens.
        boolean escape = false;
        token = switch (ch) {
            case '\'' -> {
                do {
                    ch = (char) input.read();
                    str.append(ch);
                    if (ch == '\\') {
                        escape = !escape;
                    }
                    if (ch == '\'' || ch == '"') {
                        if (escape) {
                            escape = false;
                        } else {
                            if (ch == '\'') break;
                        }
                    }
                } while (true);
                yield new Token(CHRCON, String.valueOf(str), parsingLineNo);
            }
            case '"' -> {
                do {
                    ch = (char) input.read();
                    str.append(ch);
                } while (ch != '"');
                yield new Token(STRCON, String.valueOf(str), parsingLineNo);
            }
            default -> null;
        };

        if (Character.isDigit(ch)) {
            do {
                input.mark(1);
                ch = (char) input.read();
                if (Character.isDigit(ch)) {
                    str.append(ch);
                }
            } while (Character.isDigit(ch));
            input.reset();
            token = new Token(INTCON, String.valueOf(str), parsingLineNo);
        }
        if (token != null) {
            return token;
        }

        if (Character.isAlphabetic(ch) || ch == '_') {
            do {
                input.mark(1);
                ch = (char) input.read();
                if (Character.isDigit(ch) || Character.isAlphabetic(ch) || ch == '_') {
                    str.append(ch);
                }
            } while (Character.isDigit(ch) || Character.isAlphabetic(ch) || ch == '_');
            input.reset();
            token = new Token(
                    keywordsTbl.getOrDefault(String.valueOf(str), IDENFR),
                    String.valueOf(str), parsingLineNo);
        }

        return token;
    }

}