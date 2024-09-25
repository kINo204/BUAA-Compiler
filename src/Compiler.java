import frontend.Lexer;
import frontend.Token;

import java.io.FileReader;
import java.io.IOException;

public class Compiler {
    /* Compiler execution entry point. */
    public static void main(String[] args) throws IOException {
        FileReader sourceProgram = new FileReader("testfile.txt");
        Lexer lexer = new Lexer(sourceProgram);
        while (true) {
            Token token = lexer.read();
            if (token == null) break;
        }
        lexer.close();
    }
}
