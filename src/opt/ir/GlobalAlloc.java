package opt.ir;

import ir.datastruct.operand.Var;
import mips.datastruct.MipsReg;
import opt.ir.datastruct.Unit;
import opt.ir.datastruct.Net;

import java.util.HashMap;

public class GlobalAlloc {
    private final HashMap<Net, MipsReg> allocations = new HashMap<>();

    public void addAllocations(HashMap<Net, MipsReg> allocations) {
        this.allocations.putAll(allocations);
    }

    public MipsReg query(Unit unit) {
        if (unit.operand instanceof Var var) {
            return allocations.get(var.net);
        }
        else return null;
    }
}
