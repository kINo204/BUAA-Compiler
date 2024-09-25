import frontend.Lexer;
import frontend.Token;

import java.io.IOException;

public class Compiler {
    /* Compiler execution entry point. */
    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer();
        while (true) {
            Token token = lexer.read();
            if (token == null) break;
        }
        lexer.closeLogs();
    }
}
