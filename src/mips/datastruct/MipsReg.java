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

    private final RegId id;

    private MipsReg(RegId regId) {
        this.id = regId;
    }

    @Override
    public String toString() {
        return "$" + id.toString().toLowerCase();
    }

    public enum RegId{
        ZERO,
        AT,
        V0, V1,
        A0, A1, A2, A3,
        T0, T1, T2, T3, T4, T5, T6, T7, T8, T9,
        S0, S1, S2, S3, S4, S5, S6, S7,
        K0, K1,
        GP,
        SP,
        FP,
        RA
    }
}
