package mips.datastruct;

import ir.datastruct.operand.Const;
import ir.datastruct.operand.FuncRef;
import ir.datastruct.operand.Label;

import static mips.datastruct.MipsInstr.MipsType.*;

public class MipsInstr {
    // Main fields.
    private final MipsType type;
    private MipsOperator operator = null;
    private MipsOperand res = null, first = null, second = null;

    // Supl fields.
    private String labelString = null;

    // Minimal constructor.
    public MipsInstr(MipsType type) {
        this.type = type;
    }

    public MipsInstr(MipsType type, MipsOperator op, MipsOperand res, MipsOperand first, MipsOperand second) {
        this.type = type;
        this.operator = op;
        this.res = res;
        this.first = first;
        this.second = second;
    }

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

    public static MipsInstr genMem(MipsOperator op, MipsReg reg, MipsReg base, MipsOperand offset) {
        return new MipsInstr(MipsType.MEM, op, reg, base, offset);
    }

    public static MipsInstr genMove(MipsReg to, MipsReg from) {
        return genCalc(MipsOperator.MOVE, to, from, null);
    }

    public static MipsInstr genMoveFrom(MipsOperator op, MipsReg to) {
        return genCalc(op, to, null, null);
    }

    public static MipsInstr genCalc(MipsOperator op, MipsReg res, MipsReg first, MipsOperand second) {
        return new MipsInstr(MipsType.CALC, op, res, first, second );
    }

    public static MipsInstr genJumpReg(MipsReg reg) {
        return new MipsInstr(MipsType.JR, null, reg, null);
    }

    private static MipsInstr genBranch(MipsOperator jumpOp, MipsReg first, MipsReg second, Label label) {
        MipsInstr instr = new MipsInstr(BR, label, first, second);
        instr.operator = jumpOp;
        return instr;
    }

    public static MipsInstr genJump(Label label) {
        return genBranch(MipsOperator.J, null, null, label);
    }

    public static MipsInstr genBeq(MipsReg first, MipsReg second, Label label) {
        return genBranch(MipsOperator.BEQ, first, second, label);
    }

    public static MipsInstr genBne(MipsReg first, MipsReg second, Label label) {
        return genBranch(MipsOperator.BNE, first, second, label);
    }

    public static MipsInstr genTextSeg() {
        return new MipsInstr(DOT_TEXT);
    }

    public static MipsInstr genGlobl() {
        return new MipsInstr(MipsType.DOT_GLOB);
    }

    public static MipsInstr genLabel(FuncRef funcRef) {
        MipsInstr instr = new MipsInstr(LABEL);
        instr.labelString = funcRef.funcName;
        return instr;
    }

    public static MipsInstr genLabel(Label label) {
        MipsInstr instr = new MipsInstr(LABEL);
        instr.labelString = label.name;
        return instr;
    }

    @Override
    public String toString() {
        if (type == DOT_TEXT) {
            return ".text";
        } else if (type == DOT_DATA) {
            return ".data";
        } else if (type == MipsType.DOT_GLOB) {
            return ".globl";
        } else if (type == MipsType.CALC) {
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
        } else if (type == BR) {
            StringBuilder sb = new StringBuilder();
            sb.append("\t").append(operator).append("\t");
            if (first != null) {
                sb.append(first).append(", ");
            }
            if (second != null) {
                sb.append(second).append(", ");
            }
            sb.append(res);
            return sb.toString().toLowerCase();
        } else if (type == MipsType.MEM) {
            String sb = "\t" +
                    operator +
                    "\t" +
                    res + ", " +
                    second + "(" +
                    first + ")";
            return sb.toLowerCase();
        } else if (type == MipsType.JR) {
            return String.format("\tjr\t%s", first);
        } else if (type == MipsType.LABEL) {
            return "\n" + labelString + ":";
        }
        return "\tERR_INSTRUCTION";
    }

    public enum MipsType {
        DOT_GLOB,
        DOT_TEXT,
        DOT_DATA,
        LABEL,

        /* Common operations */
        CALC,
        MEM,

        /* Procedural calls */
        BR,
        JR, JAL,
    }

    public enum MipsOperator {
        MOVE,
        ADDU, ADDI, SUBU, SUBI, MUL, DIVU,
        MFHI, MFLO,
        LI, LA,
        LW, LB, SW, SB,

        /* Branches */
        J, BNE, BEQ,
    }
}
