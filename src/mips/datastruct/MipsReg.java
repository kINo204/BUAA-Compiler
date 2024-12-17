package mips.datastruct;

import java.util.HashMap;

public class MipsReg implements MipsOperand {
    private final static HashMap<RegId, MipsReg> regs = new HashMap<>();
    static {
        for (int i = 0; i < 32; i++) {
            regs.put(RegId.values()[i], new MipsReg(RegId.values()[i]));
        }
    }

    public static MipsReg r(RegId regId) {
        return regs.get(regId);
    }

    public final RegId id;

    private MipsReg(RegId regId) {
        this.id = regId;
    }

    @Override
    public String toString() {
        return "$" + id.toString().toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MipsReg reg)) return false;

        return id == reg.id;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public enum RegId{
        zero,
        at,
        v0, v1,
        a0, a1, a2, a3,
        t0, t1, t2, t3, t4, t5, t6, t7, t8, t9,
        s0, s1, s2, s3, s4, s5, s6, s7,
        k0, k1,
        gp,
        sp,
        fp,
        ra
    }
}
