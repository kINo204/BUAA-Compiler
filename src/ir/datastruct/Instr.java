package ir.datastruct;

import ir.datastruct.operand.Label;
import ir.datastruct.operand.Operand;
import ir.datastruct.operand.Reg;
import ir.datastruct.operand.Var;

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

    static Instr genAlloc(Var var) {
        return new Instr(
                Operator.ALLOC, var.type, var, null, null
        );
    }

    static Instr genAlloc(Var var, Operand arrayLength) {
        return new Instr(
                Operator.ALLOC, var.type, var, arrayLength, null
        );
    }

    static Instr genLoad(Var var, Reg reg) {
        return new Instr(
                Operator.LOAD, var.type, reg, var, null
        );
    }

    static Instr genStore(Var var, Operand value) {
        return new Instr(
                Operator.STORE, var.type, var, value, null
        );
    }

    static Instr genLoad(Var var, Reg reg, Operand arrayIndex) {
        return new Instr(
                Operator.LOAD, var.type, reg, var, arrayIndex
        );
    }

    static Instr genStore(Var var, Operand value, Operand arrayIndex) {
        return new Instr(
                Operator.STORE, var.type, var, value, arrayIndex
        );
    }


    static Instr genGoto(Value target) {
        return new Instr(
                Operator.GOTO, null, null, new Label(target), null
        );
    }

    static Instr genGoto(Label label) {
        return new Instr(
                Operator.GOTO, null, null, label, null
        );
    }

    static Instr genGoif(Label label, Operand condValue) {
        return new Instr(
                Operator.GOIF, null, null, label, condValue
        );
    }

    static Instr genGoIfNot(Label label, Operand condValue) {
        return new Instr(
                Operator.GONT, null, null, label, condValue
        );
    }

    static Instr genGoif(Value target, Operand condValue) {
        return new Instr(
                Operator.GOIF, null, null, new Label(target), condValue
        );
    }

    static Instr genLabelDecl(Label label) {
        Instr instr = new Instr(
                Operator.LABEL, null, label, null, null
        );
        label.target = instr; // Set target of the label to this instruction!!!
        return instr;
    }

    static Instr genReturn() {
        return new Instr(Operator.RET);
    }

    static Instr genReturn(Operand returnExp) {
        return new Instr(
                Operator.RET, returnExp.type, null, returnExp, null
        );
    }

    static Instr genCalc(Operator op, Reg res, Operand main, Operand supl) {
        return new Instr(
                op, res.type, res, main, supl
        );
    }

    static Instr genMove(Operand from, Operand to) {
        return new Instr(
                Operator.MOVE, to.type, to, from, null
        );
    }

    @Override
    public String toString() {
        if (op == Operator.MOVE) {
            return res + ": " + type + " = " + main;
        } else if (op == Operator.LABEL) {
            return "label  " + res;
        } else if (op == Operator.LOAD) {
            String str = String.format("%s: %s = %s", res, type, main);
            if (supl != null) {
                str += String.format("[%s]", supl);
            }
            return str;
        } else if (op == Operator.STORE) {
            if (supl != null) {
                return String.format("%s[%s]: %s = %s", res, supl, type, main);
            } else {
                return String.format("%s: %s = %s", res, type, main);
            }
        } else {
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
    }

    public enum Type {
        i8, i32, VOID
    }

    enum Operator {
        // Memory
        ALLOC,
        LOAD,
        STORE,

        // Arithmetic
        ADD,
        SUB,
        MUL,
        DIV,
        MOD,
        OR,
        AND,
        MOVE,

        // Comparison
        LSS, LEQ, GRE, GEQ, EQL, NEQ,

        // Jumping
        LABEL,
        GOTO,
        GOIF,
        GONT,
        CALL,
        RET,
    }
}
