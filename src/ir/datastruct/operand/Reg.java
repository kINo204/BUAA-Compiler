package ir.datastruct.operand;

public class Reg implements Operand {
    private static int index = 1;

    public static void reset() {
        index = 1;
    }

    public final int number;

    public Reg() {
        number = index;
        index += 1;
    }

    @Override
    public String toString() {
        return "%" + number;
    }
}
