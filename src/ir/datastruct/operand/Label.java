package ir.datastruct.operand;

import ir.datastruct.Instr;
import mips.datastruct.MipsOperand;

public class Label extends Operand implements MipsOperand {
    private static int index = 0;
    public final String name;

    public Instr target = null;

    public static void reset() {
        index = 0;
    }

    public Label(String text) {
        name = "$L" + index + "_" + text;
        index++;
    }

    public Label() {
        name = "$L" + index;
        index++;
    }

    @Override
    public String toString() {
        return name;
    }
}
