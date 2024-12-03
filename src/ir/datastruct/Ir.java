package ir.datastruct;

import java.util.ArrayList;

public class Ir implements IrStruct {
    public Module module; // compile unit
    public ArrayList<Instr> frontInstrs = new ArrayList<>();

    @Override
    public ArrayList<Instr> genInstrs() {
        frontInstrs.addAll(module.genInstrs());
        return frontInstrs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Instr i : frontInstrs) {
            sb.append(i).append("\n");
        }
        return sb.append("\n") + module.toString();
    }
}
