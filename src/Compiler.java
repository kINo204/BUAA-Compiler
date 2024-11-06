import datastruct.ast.AstCompUnit;
import frontend.Lexer;
import frontend.Parser;
import frontend.Validator;
import io.Log;
import ir.datastruct.Ir;
import ir.IrMaker;
import mips.MipsMinimalTranslator;
import mips.MipsTranslator;
import mips.datastruct.MipsProgram;

import java.io.*;

public class Compiler {
    private static final boolean debugInfo = true;

    private static FileReader sourceProgram;
    private static Log lexerOut;
    private static Log parserOut;
    private static Log symbolOut;
    private static Log irOut;
    private static Log programOut;
    private static Log errOut;

    /* Compiler execution entry point. */
    public static void main(String[] args) throws IOException {
        configureIO();

        /* Frontend. */
        Lexer lexer = new Lexer(sourceProgram, lexerOut, errOut);

        Parser parser = new Parser(lexer, "CompUnit", parserOut, errOut);
        AstCompUnit ast = (AstCompUnit) parser.parse();

        Validator validator = new Validator(ast, errOut);
        validator.validateAst();
        symbolOut.print(validator.symTbl);

        // Execute printing errors.
        if (errOut.hasError()) {
            errOut.executePrintErrors();
            terminate(-1);
        }

        /* Mid. */
        IrMaker irMaker = new IrMaker(ast, validator.symTbl);
        Ir ir = irMaker.make();
        irOut.print(ir);

        /* Backend. */
        MipsTranslator translator = new MipsMinimalTranslator(ir);
        MipsProgram program = translator.translate();
        programOut.print(program);

        terminate(0);
    }

    private static void configureIO() throws IOException {
        sourceProgram = new FileReader("testfile.txt");

        // Configure loggers.
        Writer lexerWriter = new FileWriter("lexer.txt");
        lexerOut = new Log();
        lexerOut.addWriter("file", lexerWriter);
        lexerOut.switchLogger(debugInfo);

        Writer parserWriter = new FileWriter("parser.txt");
        parserOut = new Log();
        parserOut.addWriter("file", parserWriter);
        parserOut.switchLogger(debugInfo);

        Writer symbolWriter = new FileWriter("symbol.txt");
        symbolOut = new Log();
        symbolOut.addWriter("file", symbolWriter);
        symbolOut.switchLogger(debugInfo);

        Writer irWriter = new FileWriter("ir.ll");
        irOut = new Log();
        irOut.addWriter("file", irWriter);
        irOut.switchLogger(debugInfo);

        Writer programWriter = new FileWriter("mips.txt");
        programOut = new Log();
        programOut.addWriter("file", programWriter);
        programOut.switchLogger(true);

        Writer errWriter = new FileWriter("error.txt");
        errOut = new Log();
        errOut.addWriter("file err", errWriter);
        errOut.switchLogger(true);
    }

    private static void terminate(int status) throws IOException {
        // Closing streams.
        sourceProgram.close();

        lexerOut.close();
        parserOut.close();
        symbolOut.close();
        irOut.close();
        programOut.close();
        errOut.close();

        System.exit(status);
    }

}
