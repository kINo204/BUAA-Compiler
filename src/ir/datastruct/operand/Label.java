package ir.datastruct.operand;

import ir.datastruct.Value;

public class Label implements Operand {
    public Value target;

    public Label(Value target) {
        this.target = target;
    }
}
