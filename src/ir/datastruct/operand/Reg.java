package ir.datastruct.operand;

import ir.datastruct.Instr;

public class Reg extends Operand {
    private static int index = 1;

    public static void reset() {
        index = 1;
    }

    public final int number;

    public Reg(Instr.Type type) {
        number = index;
        index += 1;
        super.type = type;
    }

    @Override
    public String toString() {
        return "%" + number;
    }
}
