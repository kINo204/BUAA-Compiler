package ir.datastruct;

import ir.datastruct.operand.Operand;

import java.util.ArrayList;

class BasicBlock implements Value {
    ArrayList<Instr> instructions = new ArrayList<>();
    Operand conditionValue;
    BasicBlock nextDefault;
    BasicBlock nextTrue;
    BasicBlock nextFalse;

    Instr getEntry() {
        assert !instructions.isEmpty();
        return instructions.get(0);
    }

    public ArrayList<Instr> genInstrs() {
        return instructions;
    }
}
