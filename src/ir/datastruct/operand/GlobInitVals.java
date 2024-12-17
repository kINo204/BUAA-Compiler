package ir.datastruct.operand;

import mips.datastruct.MipsOperand;

import java.util.ArrayList;

public class GlobInitVals extends Operand implements MipsOperand {
    public final ArrayList<Const> initVals = new ArrayList<>();

    public void addVal(Const val) {
        initVals.add(val);
    }

    public GlobInitVals() { }

    public GlobInitVals(Const c) {
        initVals.add(c);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Const c : initVals) {
            sb.append(c).append(", ");
        }

        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
}
