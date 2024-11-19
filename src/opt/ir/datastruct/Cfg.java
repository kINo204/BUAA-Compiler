package opt.ir.datastruct;

import java.util.ArrayList;

public class Cfg {
    public final BBlock entry;
    public final BBlock exit;
    public final ArrayList<BBlock> blocks;

    public Cfg(BBlock entry, BBlock exit, ArrayList<BBlock> blocks) {
        this.entry = entry;
        this.exit = exit;
        this.blocks = blocks;
    }

    public void connect(BBlock from, BBlock to) {
        from.nextSet.add(to);
        to.prevSet.add(from);
    }

    public void connectTrue(BBlock from, BBlock to) {
        from.tarTrue = to;
        to.prevSet.add(from);
    }

    public void connectFalse(BBlock from, BBlock to) {
        from.tarFalse = to;
        to.prevSet.add(from);
    }

    public void disconnect(BBlock from, BBlock to) {
        from.nextSet.remove(to);
        to.prevSet.remove(from);
    }

    public void disconnectTrue(BBlock from, BBlock to) {
        from.tarTrue = null;
        if (from.tarFalse != to) {
            to.prevSet.remove(from);
        }
    }

    public void disconnectFalse(BBlock from, BBlock to) {
        from.tarFalse = null;
        if (from.tarTrue != to) {
            to.prevSet.remove(from);
        }
    }
}
