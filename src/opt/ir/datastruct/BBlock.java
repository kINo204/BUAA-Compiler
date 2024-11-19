package opt.ir.datastruct;

import ir.datastruct.Instr;
import ir.datastruct.operand.Operand;

import java.util.ArrayList;
import java.util.HashSet;

import static ir.datastruct.Instr.Operator.RET;

public class BBlock {
    public final ArrayList<Instr> instrs;
    public final HashSet<BBlock> nextSet = new HashSet<>();
    public final HashSet<BBlock> prevSet = new HashSet<>();

    // Info for regeneration.
    public boolean isCondJump = false;
    public Operand condition = null;
    public BBlock tarTrue = null;
    public BBlock tarFalse = null;

    public BBlock(ArrayList<Instr> instrs) {
        this.instrs = instrs;
    }

    public void addNext(BBlock block) {
        nextSet.add(block);
    }

    public void addPrev(BBlock block) {
        prevSet.add(block);
    }

    public boolean isEmpty() { return instrs.isEmpty(); }

    public boolean isRet() {
        if (instrs.isEmpty()) return false;
        return instrs.get(instrs.size() - 1).op == RET;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (Instr i : instrs) {
            sb.append("\t").append(i.toString().trim()).append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
}
