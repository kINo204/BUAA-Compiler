package mips.datastruct;

import ir.datastruct.Instr;
import ir.datastruct.operand.*;

import static ir.datastruct.Instr.Type.i8;
import static mips.datastruct.MipsInstr.MipsType.*;

public class MipsInstr {
    // Main fields.
    private final MipsType type;
    private MipsOperator operator = null;
    private MipsOperand res = null, first = null, second = null;

    // Supl fields.
    private String labelString = null;
    private String comment = null;

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

    public static MipsInstr genComment(String comment) {
        MipsInstr instr = new MipsInstr(COMMENT);
        instr.comment = comment;
        return instr;
    }

    public static MipsInstr genLi(MipsReg reg, Const val) {
        return genCalc(MipsOperator.li, reg, null, val);
    }

    public static MipsInstr genLa(MipsReg to, Var var) {
        assert var.isGlobal;
        return genCalc(MipsOperator.la, to,null, var);
    }

    public static MipsInstr genMem(MipsOperator op, MipsReg reg, MipsReg base, MipsOperand offset) {
        return new MipsInstr(MipsType.MEM, op, reg, base, offset);
    }

    public static MipsInstr genMove(MipsReg to, MipsReg from) {
        return genCalc(MipsOperator.move, to, from, null);
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

    private static MipsInstr genBranch(MipsOperator jumpOp, MipsReg first, MipsReg second, FuncRef funcRef) {
        MipsInstr instr = new MipsInstr(BR, funcRef, first, second);
        instr.operator = jumpOp;
        return instr;
    }

    public static MipsInstr genJump(Label label) {
        return genBranch(MipsOperator.j, null, null, label);
    }

    public static MipsInstr genJal(FuncRef funcRef) {
        return genBranch(MipsOperator.jal, null, null, funcRef);
    }

    public static MipsInstr genBeq(MipsReg first, MipsReg second, Label label) {
        return genBranch(MipsOperator.beq, first, second, label);
    }

    public static MipsInstr genBne(MipsReg first, MipsReg second, Label label) {
        return genBranch(MipsOperator.bne, first, second, label);
    }

    public static MipsInstr genTextSeg() {
        return new MipsInstr(DOT_TEXT);
    }

    public static MipsInstr genDataSeg() {
        return new MipsInstr(DOT_DATA);
    }

    public static MipsInstr genGlobl() {
        return new MipsInstr(MipsType.DOT_GLOB);
    }

    public static MipsInstr genGlobDecl(Var var, Instr.Type type, GlobInitVals initVals) {
        return new MipsInstr(
                GLOB, var, initVals, null
        );
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
        if (type == COMMENT) {
            return "\n\t# " + comment.strip();
        } else if (type == DOT_TEXT) {
            return ".text";
        } else if (type == DOT_DATA) {
            return ".data";
        } else if (type == DOT_GLOB) {
            return ".globl";
        } else if (type == GLOB) {
            return String.format("\t%s: %s %s",
                    res,
                    ((Var) res).type == i8 ? ".byte" : ".word",
                    first);
        } else if (type == CALC) {
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
            return sb.toString();
        } else if (type == BR) {
            StringBuilder sb = new StringBuilder();
            sb.append("\t").append(operator).append("\t");
            if (first != null) {
                sb.append(first).append(", ");
            }
            if (second != null) {
                sb.append(second).append(", ");
            }
            sb.append(res).append("\n");
            return sb.toString();
        } else if (type == MEM) {
            return "\t" +
                    operator +
                    "\t" +
                    res + ", " +
                    second + "(" +
                    first + ")";
        } else if (type == JR) {
            return String.format("\tjr\t%s\n", first);
        } else if (type == LABEL) {
            return labelString + ":";
        }
        return "\tERR_INSTRUCTION";
    }

    public enum MipsType {
        COMMENT,
        DOT_GLOB,
        DOT_TEXT,
        DOT_DATA,
        LABEL,
        GLOB,

        /* Common operations */
        CALC,
        MEM,

        /* Procedural calls */
        BR,
        JR,
    }

    public enum MipsOperator {
        move,
        addu, addi, subu, subiu, mulu, div, sll,
        mfhi, mflo,
        li, la,
        lw, lb, sw, sb,
        slt, sle, sgt, sge, seq, sne,

        /* Branches */
        j, jal, bne, beq,
    }
}
