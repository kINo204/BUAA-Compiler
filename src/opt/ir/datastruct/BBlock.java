package opt.ir.datastruct;

import ir.datastruct.Instr;
import ir.datastruct.operand.Operand;

import java.util.ArrayList;
import java.util.HashSet;

import static ir.datastruct.Instr.Operator.RET;

public class BBlock {
    public static int count = 0;
    public final int id;
    public final ArrayList<Instr> instrs;
    public final HashSet<BBlock> nextSet = new HashSet<>();
    public final HashSet<BBlock> prevSet = new HashSet<>();

    // Info for regeneration.
    public boolean isCondJump = false;
    public Operand condition = null;
    public BBlock tarTrue = null;
    public BBlock tarFalse = null;

    public BBlock(ArrayList<Instr> instrs) {
        this.id = count++;
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
        StringBuilder strInstrs = new StringBuilder();
        for (Instr i : instrs) {
            strInstrs.append(i.toString().trim()).append("\\l");
        }

        StringBuilder strConnections = new StringBuilder();
        if (isCondJump) {
            strConnections.append(String.format(
                    "\tB%d -> B%d [label=\"%s T\"]\n",
                    id, tarTrue.id, condition
            ));
            strConnections.append(String.format(
                    "\tB%d -> B%d [label=\"%s F\"]",
                    id, tarFalse.id, condition
            ));
        } else {
            if (isRet()) {
                strConnections.append("\tB").append(id).append(" -> ").append(
                        "Exit"
                );
            } else {
                strConnections.append("\tB").append(id).append(" -> ").append("B").append(
                        ((BBlock) nextSet.toArray()[0]).id
                );
            }
        }

        return String.format("""
                    B%d [shape=box xlabel="B%d" label="%s"]
                %s
                
                """, id, id, strInstrs, strConnections);
    }
}
