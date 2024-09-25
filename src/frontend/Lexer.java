package frontend;

import io.Log;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import static frontend.Token.TokenId.*;

public class Lexer {
    public Lexer(Reader reader) throws IOException {
        input = new BufferedReader(reader);
        if (!input.markSupported()) {
            System.err.println("Lexer marking not supported for this input stream!");
        }
        loggerOut.switchLogger(true);
        loggerOut.addFileWriter("lexer out", "lexer.txt");
        loggerErr.switchLogger(true);
        loggerErr.addFileWriter("lexer err", "error.txt");
    }

    public Lexer(String inputString) throws IOException {
        input = new BufferedReader(new StringReader(inputString));
        if (!input.markSupported()) {
            System.err.println("Lexer marking not supported for this input stream!");
        }
        loggerOut.switchLogger(true);
        loggerOut.addFileWriter("lexer out", "lexer.txt");
        loggerErr.switchLogger(true);
        loggerErr.addFileWriter("lexer err", "error.txt");
    }

    private int currentLine = 1;
    private final LinkedList<Token> readTokens = new LinkedList<>(); // LIFO, tokens already read in lookAhead.
    private final BufferedReader input;
    private final Log loggerOut = new Log();
    private final Log loggerErr = new Log();
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

    public Token read() throws IOException {
        if (!readTokens.isEmpty()) {
            return readTokens.removeFirst();
        } else {
            return parseToken();
        }
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
            return readTokens.get(offset);
        }
        // Token not parsed yet:
        // If EOF has been reached, return.
        if (!readTokens.isEmpty() && readTokens.getLast() == null) {
            return null;
        }
        // Else continue parsing.
        for (int i = 0; i <= offset - size; i++) {
            readTokens.addLast(parseToken());
            // If a null be added, we reach EOF. No need for parsing more tokens.
            if (readTokens.getLast() == null)
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
                    return null; // EOF
                ch = (char) ich;
                if (ch == '\n')
                    currentLine ++;
            } while (String.valueOf(ch).isBlank());

            // Deal with comments.
            if (ch == '/') {
                input.mark(1);
                ch = (char) input.read();
                if (ch == '/') {
                    int ich;
                    do {
                        ich = input.read();
                    } while (ich != '\n' && ich != -1);
                } else if (ch == '*') {
                    do {
                        ch = (char) input.read();
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
            case ';' -> new Token(SEMICN, ";");
            case ',' -> new Token(COMMA, ",");
            case '(' -> new Token(LPARENT, "(");
            case ')' -> new Token(RPARENT, ")");
            case '[' -> new Token(LBRACK, "[");
            case ']' -> new Token(RBRACK, "]");
            case '{' -> new Token(LBRACE, "{");
            case '}' -> new Token(RBRACE, "}");
            case '+' -> new Token(PLUS, "+");
            case '-' -> new Token(MINU, "-");
            case '*' -> new Token(MULT, "*");
            case '/' -> new Token(DIV, "/");
            case '%' -> new Token(MOD, "%");
            default -> null;
        };
        if (token != null) {
            loggerOut.println(token);
            return token;
        }

        StringBuilder str = new StringBuilder(String.valueOf(ch));

        // Finite look-ahead tokens.
        switch (ch) {
            case '>':
                input.mark(1);
                str.append((char) input.read());
                if (str.toString().equals(">=")) {
                    token = new Token(GEQ, ">=");
                } else {
                    input.reset();
                    token = new Token(GRE, ">");
                }
                break;
            case '<':
                input.mark(1);
                str.append((char) input.read());
                if (str.toString().equals("<=")) {
                    token = new Token(LEQ, "<=");
                } else {
                    input.reset();
                    token = new Token(LSS, "<");
                }
                break;
            case '=':
                input.mark(1);
                str.append((char) input.read());
                if (str.toString().equals("==")) {
                    token = new Token(EQL, "==");
                } else {
                    input.reset();
                    token = new Token(ASSIGN, "=");
                }
                break;
            case '!':
                input.mark(1);
                str.append((char) input.read());
                if (str.toString().equals("!=")) {
                    token = new Token(NEQ, "!=");
                } else {
                    input.reset();
                    token = new Token(NOT, "!");
                }
                break;
            case '&':
                input.mark(1);
                str.append((char) input.read());
                if (str.toString().equals("&&")) {
                    token = new Token(AND, "&&");
                } else {
                    input.reset();
                    token = new Token(AND, "&&");
                    loggerErr.println(currentLine + " a");
                }
                break;
            case '|':
                input.mark(1);
                str.append((char) input.read());
                if (str.toString().equals("||")) {
                    token = new Token(OR, "||");
                } else {
                    input.reset();
                    token = new Token(OR, "&&");
                    loggerErr.println(currentLine + " a");
                }
                break;
        }
        if (token != null) {
            loggerOut.println(token);
            return token;
        }

        // Looping look-ahead tokens.
        char last;
        token = switch (ch) {
            case '\'' -> {
                do {
                    last = ch;
                    ch = (char) input.read();
                    str.append(ch);
                } while (ch != '\'' || last == '\\');
                yield new Token(CHRCON, String.valueOf(str));
            }
            case '"' -> {
                do {
                    last = ch;
                    ch = (char) input.read();
                    str.append(ch);
                } while (ch != '"' || last == '\\');
                yield new Token(STRCON, String.valueOf(str));
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
            token = new Token(INTCON, String.valueOf(str));
        }
        if (token != null) {
            loggerOut.println(token);
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
                    String.valueOf(str));
        }

        if (token != null) { loggerOut.println(token); }
        return token;
    }

    public void close() throws IOException {
        input.close();
        loggerOut.close();
        loggerErr.close();
    }
}