import frontend.datastruct.ast.AstCompUnit;
import frontend.Lexer;
import frontend.Parser;
import frontend.Validator;
import opt.ir.IrOptimizer;
import utils.Log;
import ir.datastruct.Ir;
import ir.IrMaker;
import mips.MipsMinimalTranslator;
import mips.MipsTranslator;
import mips.datastruct.MipsProgram;

import java.io.*;

public class Compiler {
    private static final boolean debugInfo = true;

    private static FileReader sourceProgram;
    private static Log parserOut;
    private static Log symbolOut;
    private static Log irOut;
    private static Log irOptInfoOutput;
    private static Log programOut;
    private static Log errOut;

    /* Compiler execution entry point. */
    public static void main(String[] args) throws IOException {
        configureIO();

        /* Frontend. */
        Lexer lexer = new Lexer(sourceProgram, parserOut, errOut);

        Parser parser = new Parser(lexer, "CompUnit", parserOut, errOut);
        AstCompUnit ast = (AstCompUnit) parser.parse();
        sourceProgram.close();
        parserOut.close();

        Validator validator = new Validator(ast, errOut);
        validator.validateAst();
        symbolOut.print(validator.symTbl);
        symbolOut.close();

        // Execute printing errors.
        if (errOut.hasError()) {
            errOut.executePrintErrors();
            errOut.close();
            System.exit(-1);
        }

        /* Mid. */
        IrMaker irMaker = new IrMaker(ast, validator.symTbl);
        Ir ir = irMaker.make();
        irOut.print(ir); // unoptimized IR
        irOut.close();

        IrOptimizer irOptimizer = new IrOptimizer(ir, irOptInfoOutput);
        irOptimizer.optimize();
        irOptInfoOutput.print(ir); // unoptimized IR
        irOptInfoOutput.close();

        /* Backend. */
        MipsTranslator translator = new MipsMinimalTranslator(ir); // TODO
        MipsProgram program = translator.translate();
        programOut.print(program);
        programOut.close();

        errOut.close();
        System.exit(0);
    }

    private static void configureIO() throws IOException {
        sourceProgram = new FileReader("testfile.txt");

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

        Writer irOptInfoWriter = new FileWriter("ir_opt_info.ll");
        irOptInfoOutput = new Log();
        irOptInfoOutput.addWriter("file", irOptInfoWriter);
        irOptInfoOutput.switchLogger(debugInfo);

        Writer programWriter = new FileWriter("mips.txt");
        programOut = new Log();
        programOut.addWriter("file", programWriter);
        programOut.switchLogger(true);

        Writer errWriter = new FileWriter("error.txt");
        errOut = new Log();
        errOut.addWriter("file err", errWriter);
        errOut.switchLogger(true);
    }
}
