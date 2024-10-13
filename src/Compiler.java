import datastruct.ast.AstCompUnit;
import frontend.Lexer;
import frontend.Parser;
import frontend.Validator;
import io.Log;

import java.io.*;

public class Compiler {
    /* Compiler execution entry point. */
    public static void main(String[] args) throws IOException {
        FileReader sourceProgram = new FileReader("testfile.txt");
        Writer stdout = new PrintWriter(System.out);
        Writer stderr = new PrintWriter(System.err);
        Writer symbolOut = new FileWriter("symbol.txt");
        Writer err = new FileWriter("error.txt");

        Log o = new Log();
//        o.addWriter("stdout", stdout);
//        o.addWriter("file out", parserOut);
//        o.switchLogger(true);

        Log o1 = new Log();
//        o1.addWriter("stdout", stdout);
        o1.addWriter("file out", symbolOut);
        o1.switchLogger(true);

        Log e = new Log();
//        e.addWriter("stderr", stderr);
        e.addWriter("file err", err);
        e.switchLogger(true);

        Lexer lexer = new Lexer(sourceProgram, o, e);

        Parser parser = new Parser(lexer, "CompUnit", o, e);
        AstCompUnit ast = (AstCompUnit) parser.parse();
        Validator validator = new Validator(ast, o, e);
        validator.validateAst();
        o1.print(validator.symTbl);

        o.executePrint();
        o1.executePrint();
        e.executePrint();

        sourceProgram.close();
        o.close();
        o1.close();
        e.close();
    }
}
