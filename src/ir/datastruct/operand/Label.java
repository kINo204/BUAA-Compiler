package ir.datastruct.operand;

import ir.datastruct.Instr;
import mips.datastruct.MipsOperand;

public class Label extends Operand implements MipsOperand {
    private static int index = 0;
    public final String name;

    public Instr target = null;

    public Label(String text) {
        name = "$l" + index + "_" + text;
        index++;
    }

    public Label(Instr target) {
        name = "$L" + index;
        index++;
        this.target = target;
    }

    @Override
    public String toString() {
        return name;
    }
}
