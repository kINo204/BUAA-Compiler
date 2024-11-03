import datastruct.ast.AstCompUnit;
import frontend.Lexer;
import frontend.Parser;
import frontend.Validator;
import io.Log;
import ir.datastruct.Ir;
import ir.datastruct.IrMaker;

import java.io.*;

public class Compiler {
    /* Compiler execution entry point. */
    public static void main(String[] args) throws IOException {
        FileReader sourceProgram = new FileReader("testfile.txt");

        // Configure loggers.
        Writer lexerWriter = new FileWriter("lexer.txt");
        Log lexerOut = new Log();
        lexerOut.addWriter("file", lexerWriter);
        lexerOut.switchLogger(true);

        Writer parserWriter = new FileWriter("parser.txt");
        Log parserOut = new Log();
        parserOut.addWriter("file", parserWriter);
        parserOut.switchLogger(true);

        Writer symbolWriter = new FileWriter("symbol.txt");
        Log symbolOut = new Log();
        symbolOut.addWriter("file", symbolWriter);
        symbolOut.switchLogger(true);

        Writer irWriter = new FileWriter("ir.ll");
        Log irOut = new Log();
        irOut.addWriter("file", irWriter);
        irOut.switchLogger(true);

        Writer programWriter = new FileWriter("mips.txt");
        Log programOut = new Log();
        programOut.addWriter("file", programWriter);
        programOut.switchLogger(true);

        Writer err = new FileWriter("error.txt");
        Log e = new Log();
        e.addWriter("file err", err);
        e.switchLogger(true);

        // Connect the compiler.
        Lexer lexer = new Lexer(sourceProgram, lexerOut, e);

        Parser parser = new Parser(lexer, "CompUnit", parserOut, e);
        AstCompUnit ast = (AstCompUnit) parser.parse();

        Validator validator = new Validator(ast, e);
        validator.validateAst();
        symbolOut.print(validator.symTbl);

        IrMaker irMaker = new IrMaker(ast, validator.symTbl);
        Ir ir = irMaker.make();
        irOut.print(ir);

        // Execute printing errors.
        e.executePrintErrors();

        // Closing streams.
        sourceProgram.close();
        lexerOut.close();
        parserOut.close();
        symbolOut.close();
        irOut.close();
        programOut.close();
        e.close();
    }
}
