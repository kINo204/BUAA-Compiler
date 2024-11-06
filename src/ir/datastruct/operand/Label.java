package ir.datastruct.operand;

import ir.datastruct.Instr;
import ir.datastruct.IrStruct;

public class Label extends Operand {
    private static int index = 0;
    private final String name;

    public Instr target = null;

    public Label(String text) {
        name = "$L" + index + "_" + text;
        index++;
    }

    public Label(Instr target) {
        name = "_" + index;
        index++;
        this.target = target;
    }

    @Override
    public String toString() {
        return name;
    }
}
