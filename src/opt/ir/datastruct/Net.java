package opt.ir.datastruct;

import ir.datastruct.Instr;
import ir.datastruct.operand.Var;

import java.util.HashSet;

public class Net {
    public final Var var;
    public final HashSet<Instr> defs;
    public final HashSet<Instr> usages;

    public Net(Var var, HashSet<Instr> key, HashSet<Instr> value) {
        this.var = var;
        defs = key;
        usages = value;
    }

    @Override
    public String toString() {
        StringBuilder sbDefs = new StringBuilder();
        for (Instr def : defs) {
            sbDefs.append(def).append("\n");
        }
        if (!sbDefs.isEmpty())
            sbDefs.deleteCharAt(sbDefs.length() - 1);

        StringBuilder sbUsages = new StringBuilder();
        for (Instr use : usages) {
            sbUsages.append(use).append("\n");
        }
        if (!sbUsages.isEmpty())
            sbUsages.deleteCharAt(sbUsages.length() - 1);

        return String.format("""
                Net %s: {
                %s
                } -> {
                %s
                }
                """, var, sbDefs, sbUsages);
    }
}
