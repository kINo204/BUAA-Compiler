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
        StringBuilder sbFront = new StringBuilder();
        if (!frontInstrs.isEmpty()) {
            for (Instr i : frontInstrs) {
                sbFront.append(i).append("\n");
            }
            sbFront.append("\n");
        }

        return sbFront + module.toString();
    }
}
