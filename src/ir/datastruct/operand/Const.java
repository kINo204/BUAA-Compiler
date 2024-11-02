package ir.datastruct.operand;

public class Const implements Operand {
    public Character ch = null;
    public Integer num = null;

    public Const(int num) {
        this.num = num;
    }

    public Const(char ch) {
        this.ch = ch;
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
