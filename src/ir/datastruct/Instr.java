package ir.datastruct;

import datastruct.symbol.Symbol;
import ir.datastruct.operand.Label;
import ir.datastruct.operand.Operand;
import ir.datastruct.operand.Reg;

import static datastruct.symbol.Symbol.SymId.*;

public class Instr implements Value {
    Operator op;
    Type type = null;
    Operand res = null, main = null, supl = null;

    Instr(Operator op) {
        this.op = op;
    }

    Instr(Operator op, Type t, Operand r, Operand m, Operand s) {
        this.op = op;
        type = t;
        res = r;
        main = m;
        supl = s;
    }

    static Instr genGoto(Value target) {
        return new Instr(
                Operator.GOTO, null, null, new Label(target), null
        );
    }

    static Instr genGoif(Value target, Operand condValue) {
        return new Instr(
                Operator.GOIF, null, null, new Label(target), condValue
        );
    }

    static Instr genReturn() {
        return new Instr(Operator.RET);
    }

    static Instr genReturn(Operand returnExp, Symbol.SymId typeToken) {
        Type t = typeToken == Int || typeToken == ConstInt ? Type.i32 :
                typeToken == Char || typeToken == ConstChar ? Type.i8 :
                        null; // Error type!
        return new Instr(
                Operator.RET, t, null, returnExp, null
        );
    }

    static Instr genCalc(Operator op, Symbol.SymId typeToken, Reg res, Operand main, Operand supl) {
        Type t = typeToken == Int || typeToken == ConstInt ? Type.i32 :
                typeToken == Char || typeToken == ConstChar ? Type.i8 :
                        null; // Error type!
        return new Instr(
                op, t, res, main, supl
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (res != null) {
            sb.append(String.format("%s = %s", res.toString().toLowerCase(), op.toString().toLowerCase()));
        } else {
            sb.append(String.format("%s", op.toString().toLowerCase()));
        }
        if (type != null) {
            sb.append(String.format(": %s", type.toString().toLowerCase()));
        }
        if (main != null) {
            sb.append(String.format("  %s", main.toString().toLowerCase()));
        }
        if (supl != null) {
            sb.append(String.format(", %s", supl.toString().toLowerCase()));
        }
        return sb.toString();
    }

    enum Type {
        i8, i32, VOID
    }

    enum Operator {
        // Memory
        ALLOCA,
        LOD,
        STO,

        // Arithmetic
        ADD,
        SUB,
        MUL,
        DIV,
        MOD,

        // Comparison
        LSS, LEQ, GRE, GEQ, EQL, NEQ,

        // Jumping
        GOTO,
        GOIF,
        CALL,
        RET,
    }
}
