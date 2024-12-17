package ir.datastruct.operand;

import ir.datastruct.Instr;
import mips.datastruct.MipsOperand;

public class Label extends Operand implements MipsOperand {
    private static int index = 0;
    public final String name;

    public Instr target = null;

    public static int getInd() {
        return index;
    }

    public static void resetInd(int labelStartingInd) {
        index = labelStartingInd;
    }

    public Label(String comment) {
        name = "$L" + index + "_" + comment;
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
