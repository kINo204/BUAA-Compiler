package mips.datastruct;

import ir.datastruct.operand.Const;
import ir.datastruct.operand.FuncRef;
import ir.datastruct.operand.Label;
import ir.datastruct.operand.Operand;

public class MipsInstr {
    private final MipsType type;
    private final MipsOperator operator;
    private final MipsOperand res, first, second;

    // Constructor for calculation instructions.
    public MipsInstr(MipsType type, MipsOperator op, MipsOperand res, MipsOperand first, MipsOperand second) {
        this.type = type;
        this.operator = op;
        this.res = res;
        this.first = first;
        this.second = second;
    }

    // Common constructor.
    public MipsInstr(MipsType type, MipsOperand res, MipsOperand first, MipsOperand second) {
        this.type = type;
        this.operator = null;
        this.res = res;
        this.first = first;
        this.second = second;
    }

    public static MipsInstr genLi(MipsReg reg, Const val) {
        return genCalc(MipsOperator.LI, reg, null, val);
    }

    public static MipsInstr genMem(MipsOperator op, MipsReg res, MipsReg base, MipsOperand offset) {
        return new MipsInstr(MipsType.MEM, op, res, base, offset);
    }

    public static MipsInstr genCalc(MipsOperator op, MipsReg res, MipsReg first, MipsOperand second) {
        return new MipsInstr(MipsType.CALC, op, res, first, second );
    }

    public static MipsInstr genJumpReg(MipsReg reg) {
        return new MipsInstr(MipsType.JR, null, reg, null);
    }

    public static MipsInstr genLabel(FuncRef funcRef) {
        return new MipsInstr(MipsType.LABEL, funcRef, null, null);
    }

    @Override
    public String toString() {
        if (type == MipsType.CALC) {
            StringBuilder sb = new StringBuilder();
            sb.append("\t").append(operator).append("\t");
            if (res != null) {
                sb.append(res);
            }
            if (first != null) {
                sb.append(", ").append(first);
            }
            if (second != null) {
                sb.append(", ").append(second);
            }
            return sb.toString().toLowerCase();
        } else if (type == MipsType.MEM) {

        } else if (type == MipsType.JR) {
            return String.format("\tjr\t%s", first);
        } else if (type == MipsType.LABEL) {
            if (res instanceof FuncRef funcRef) {
                return funcRef.funcName + ":";
            } else if (res instanceof Label label) {
                return label + ":";
            }
        }
        return "err instr";
    }

    public enum MipsType {
        DOT_GLOB,
        DOT_TEXT,
        DOT_DATA,
        LABEL,
        CALC,
        MEM,

        // TODO Branches
        J,
        JR,
        JAL,
    }

    public enum MipsOperator {
        LI, LA,
        LW, LB, SW, SB,
    }
}
