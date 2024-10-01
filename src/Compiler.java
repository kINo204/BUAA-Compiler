import frontend.Lexer;
import frontend.Parser;
import io.Log;

import java.io.*;

public class Compiler {
    /* Compiler execution entry point. */
    public static void main(String[] args) throws IOException {
        FileReader sourceProgram = new FileReader("testfile.txt");
        Writer stdout = new PrintWriter(System.out);
        Writer stderr = new PrintWriter(System.err);
        Writer parserOut = new FileWriter("parser.txt");
        Writer err = new FileWriter("error.txt");

        Log o = new Log();
        // o.addWriter("stdout", stdout);
        o.addWriter("file out", parserOut);
        o.switchLogger(true);

        Log e = new Log();
        // e.addWriter("stderr", stderr);
        e.addWriter("file err", err);
        e.switchLogger(true);

        Lexer lexer = new Lexer(sourceProgram, o, e);
        Parser parser = new Parser(lexer, "CompUnit", o, e);

        parser.parse();
        parser.close();
    }
}
