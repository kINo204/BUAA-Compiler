package ir.datastruct;

import java.util.ArrayList;

public class Ir implements IrStruct {
    public Module module; // compile unit

    @Override
    public ArrayList<Instr> genInstrs() {
        return module.genInstrs();
    }

    @Override
    public String toString() {
        return module.toString();
    }
}
