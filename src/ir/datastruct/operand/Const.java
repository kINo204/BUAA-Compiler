package ir.datastruct.operand;

import mips.datastruct.MipsOperand;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Const aConst)) return false;
        return Objects.equals(ch, aConst.ch) && Objects.equals(num, aConst.num);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ch, num);
    }
}
