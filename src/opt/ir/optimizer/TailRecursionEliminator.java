package opt.ir.optimizer;

import ir.datastruct.Instr;
import ir.datastruct.operand.FuncRef;
import ir.datastruct.operand.Label;
import ir.datastruct.operand.Operand;
import ir.datastruct.operand.Var;
import opt.ir.datastruct.BBlock;
import opt.ir.datastruct.Cfg;

import java.util.ArrayList;
import java.util.HashSet;

public class TailRecursionEliminator {
    private final Cfg cfg;

    public TailRecursionEliminator(Cfg cfg) {
        this.cfg = cfg;
    }

    public boolean run() {
        boolean mod = false;
        Label startOfFunc = null;

        for (BBlock block : cfg.blocks) {
            if (!(
                    block.isRet()
                            && block.instrs.size() >= 2
                            && block.instrs.get(block.instrs.size() - 2).op == Instr.Operator.CALL
                            && ((FuncRef) block.instrs.get(block.instrs.size() - 2).main).funcName.equals(cfg.funcName))
            ) continue;

            mod = true;
            if (startOfFunc == null) startOfFunc = new Label();
            assert cfg.entry.nextSet.size() == 1;
            BBlock firstBlock = ((BBlock) cfg.entry.nextSet.toArray()[0]);
            firstBlock.instrs.add(0,
                    Instr.genLabelDecl(startOfFunc));

            block.instrs.remove(block.instrs.size() - 1); // Return
            Instr call = block.instrs.remove(block.instrs.size() - 1); // Call

            // Find params and turn into assignments.
            ArrayList<Operand> rParams = new ArrayList<>();
            int i = block.instrs.size() - 1;
            while (block.instrs.get(i).op == Instr.Operator.PARAM) {
                rParams.add(block.instrs.get(i).main);
                i--;
            }
            assert rParams.size() == cfg.funcRef.params.size();
            for (i = 0; i < rParams.size(); i++) {
                block.instrs.remove(block.instrs.size() - 1);
            }

            for (i = 0; i < rParams.size(); i++) {
                Var fp = cfg.funcRef.params.get(i);
                Operand rp = rParams.get(rParams.size() - 1 - i);
                if (!fp.equals(rp))
                    block.instrs.add(Instr.genMove(fp, rp));
            }

            block.nextSet.clear();
            block.nextSet.add(firstBlock);
        }
        return mod;
    }
}
