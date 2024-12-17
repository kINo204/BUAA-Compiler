package opt.mips;

import mips.datastruct.MipsInstr;
import mips.datastruct.MipsProgram;
import mips.datastruct.MipsReg;

import java.util.Iterator;

import static mips.datastruct.MipsInstr.MipsOperator.move;

public class MipsOptimizer {
    private final MipsProgram program;

    public MipsOptimizer(MipsProgram program) {
        this.program = program;
    }

    public void optimize() {
        program.instrs.removeIf(instr ->
                instr.operator == move && instr.res.equals(instr.first));
    }
}
