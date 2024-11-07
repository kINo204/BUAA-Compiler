package mips.datastruct;

import mips.MipsUtils;

import java.util.ArrayList;

public class MipsProgram {
    private final ArrayList<MipsInstr> instrs = new ArrayList<>();

    public void append(MipsInstr instr) {
        instrs.add(instr);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Add system wrapper.
        sb.append(MipsUtils.osWrapper);

        // Language libs.
        sb.append(MipsUtils.lib_io);

        for (MipsInstr i : instrs) {
            sb.append(i.toString()).append("\n");
        }

        return sb.toString();
    }
}
