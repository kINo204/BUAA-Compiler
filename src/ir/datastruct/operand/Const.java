package ir.datastruct.operand;

import mips.datastruct.MipsOperand;

import static ir.datastruct.Instr.Type.i32;
import static ir.datastruct.Instr.Type.i8;

public class Const extends Operand implements MipsOperand {
    public Character ch = null;
    public Integer num = null;

    public Const(int num) {
        this.num = num;
        super.type = i32;
    }

    public Const(char ch) {
        this.ch = ch;
        super.type = i8;
    }

    @Override
    public String toString() {
        if (ch != null) {
            return "'" + ch + "'";
        } else {
            return num.toString();
        }
    }
}
