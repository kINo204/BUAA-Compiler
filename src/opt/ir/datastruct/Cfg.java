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
}
