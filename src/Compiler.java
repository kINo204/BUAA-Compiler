import frontend.datastruct.ast.AstCompUnit;
import frontend.Lexer;
import frontend.Parser;
import frontend.Validator;
import mips.MipsRealTranslator;
import opt.ir.IrOptUtils;
import opt.ir.IrOptimizer;
import opt.mips.MipsOptimizer;
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
    private static Log irOptOutput;
    private static Log irOptInfoOutput;
    private static Log programOut;
    private static Log errOut;

    /* Compiler execution entry point. */
    public static void main(String[] args) throws IOException {
        boolean optOn = args.length == 0 || args[0].equals("O");
        if (args.length > 0)
            IrOptUtils.genBlockExCounter = args[1].equals("P");

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
        if (optOn) {
            irOptimizer.optimize();
        }
        irOptOutput.print(ir); // unoptimized IR
        irOptOutput.close();
        irOptInfoOutput.close();

        /* Backend. */
        MipsTranslator translator = optOn ? new MipsRealTranslator(ir, irOptimizer.globalAlloc) : new MipsMinimalTranslator(ir);
        MipsProgram program = translator.translate();

        MipsOptimizer mipsOptimizer = new MipsOptimizer(program);
        if (optOn) {
            mipsOptimizer.optimize();
        }

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

        Writer irOptWriter = new FileWriter("ir_opt.ll");
        irOptOutput = new Log();
        irOptOutput.addWriter("file", irOptWriter);
        irOptOutput.switchLogger(debugInfo);

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
