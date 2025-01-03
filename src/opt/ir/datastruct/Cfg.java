package opt.ir.datastruct;

import ir.datastruct.Instr;
import ir.datastruct.operand.FuncRef;

import java.util.ArrayList;
import java.util.HashSet;

public class Cfg {
    public final FuncRef funcRef;
    public final String funcName;
    public final BBlock entry;
    public final BBlock exit;
    public final ArrayList<BBlock> blocks;

    public Cfg(BBlock entry, BBlock exit, ArrayList<BBlock> blocks, Instr funcDef) {
        this.funcRef = ((FuncRef) funcDef.res);
        this.entry = entry;
        this.exit = exit;
        this.blocks = blocks;
        this.funcName = funcRef.funcName;
    }

    /**
     * High cost search! Use with caution.
     */
    public BBlock blockOfInstr(Instr instr) {
        HashSet<BBlock> toSearch = new HashSet<>(blocks);
        toSearch.add(entry);
        toSearch.add(exit);
        for (BBlock block : toSearch) {
            if (block.instrs.contains(instr)) {
                return block;
            }
        }
        return null;
    }

    public void connect(BBlock from, BBlock to) {
        from.nextSet.add(to);
        to.prevSet.add(from);
    }

    public void connectTrue(BBlock from, BBlock to) {
        from.tarTrue = to;
        from.nextSet.add(to);
        to.prevSet.add(from);
    }

    public void connectFalse(BBlock from, BBlock to) {
        from.tarFalse = to;
        from.nextSet.add(to);
        to.prevSet.add(from);
    }

    public void disconnect(BBlock from, BBlock to) {
        from.nextSet.remove(to);
        to.prevSet.remove(from);
    }

    public void disconnectTrue(BBlock from, BBlock to) {
        from.tarTrue = null;
        from.nextSet.remove(to);
        if (from.tarFalse != to) {
            to.prevSet.remove(from);
        }
    }

    public void disconnectFalse(BBlock from, BBlock to) {
        from.tarFalse = null;
        from.nextSet.remove(to);
        if (from.tarTrue != to) {
            to.prevSet.remove(from);
        }
    }

    @Override
    public String toString() {
        StringBuilder strEntry = new StringBuilder();
        for (BBlock block : entry.nextSet) {
            strEntry.append(String.format(
                    "\tEntry -> B%d\n", block.id
            ));
        }
        StringBuilder sb = new StringBuilder();
        for (BBlock block : blocks) {
            sb.append(block);
        }
        return String.format(
        """
        digraph %s {
            graph [dpi=320]
            Entry [shape=ellipse]
            Exit [shape=ellipse]
            
        %s
        %s}
        """, funcName, strEntry, sb
    );
}
}
