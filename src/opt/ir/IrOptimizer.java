package opt.ir;

import ir.datastruct.Function;
import ir.datastruct.Ir;
import ir.datastruct.operand.Label;
import utils.Log;

import java.io.IOException;

public class IrOptimizer {
    // Configs
    final static boolean genBlockExCounter = true;

    private final Ir ir;

    public IrOptimizer(Ir ir, Log log) {
        this.ir = ir;
        funcOptimizer = new IrFuncOptimizer(log);
    }

    public void optimize() throws IOException {
        optFunctions();
    }

    /* Optimizer components */
    private final IrFuncOptimizer funcOptimizer;
    private void optFunctions() throws IOException {
        Label.resetInd(0);
        for (Function function : ir.module.functions) {
            funcOptimizer.injectInstr(function.genInstrs());
            function.instrs = funcOptimizer.optimize(); // TODO
        }
    }
}
