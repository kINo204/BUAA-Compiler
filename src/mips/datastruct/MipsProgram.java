package mips.datastruct;

import mips.MipsUtils;

import java.util.ArrayList;

public class MipsProgram {
    public final ArrayList<MipsInstr> instrs = new ArrayList<>();

    public void append(MipsInstr instr) {
        instrs.add(instr);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Add system wrapper.
        sb.append(MipsUtils.osWrapper);

        for (MipsInstr i : instrs) {
            sb.append(i).append("\n");
        }
        sb.append("\n");

        // Language libs.
        sb.append(MipsUtils.lib_io);

        return sb.toString();
    }
}
