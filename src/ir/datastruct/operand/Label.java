package ir.datastruct.operand;

import ir.datastruct.Value;

public class Label extends Operand {
    private static int index = 0;
    private final String name;

    public Value target = null;

    public Label(String text) {
        name = "$L" + index + "_" + text;
        index++;
    }

    public Label(Value target) {
        name = "_" + index;
        index++;
        this.target = target;
    }

    @Override
    public String toString() {
        return name;
    }
}
