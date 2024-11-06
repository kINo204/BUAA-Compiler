package ir.datastruct;

import ir.datastruct.operand.*;

public class Instr implements Value {
    Operator op;
    Type type = null;
    Operand res = null, main = null, supl = null;

    Instr(Operator op) {
        this.op = op;
    }

    Instr(Operator operator, Type type, Operand res, Operand main, Operand supl) {
        this.op = operator;
        this.type = type;
        this.res = res;
        this.main = main;
        this.supl = supl;
    }

    static Instr genFuncDef(FuncRef funcRef) {
        return new Instr(
                Operator.FUNC, funcRef.type, funcRef, null, null
        );
    }

    static Instr genParam(Operand param) {
        return new Instr(
                Operator.PARAM, null, null, param, null
        );
    }

    static Instr genFuncCall(Operand res, FuncRef funcRef) {
        return new Instr(
                Operator.CALL, res.type, res, funcRef, null
        );
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


    static Instr genGoto(Label label) {
        return new Instr(
                Operator.GOTO, null, label, null, null
        );
    }

    static Instr genGoif(Label label, Operand condValue) {
        return new Instr(
                Operator.GOIF, null, label, condValue, null
        );
    }

    static Instr genGoIfNot(Label label, Operand condValue) {
        return new Instr(
                Operator.GONT, null, label, condValue, null
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
        if (op == Operator.FUNC) {
            return "fun " + res + ":";
        } else if (op == Operator.MOVE) {
            return "\t" + res + ": " + type + " = " + main;
        } else if (op == Operator.LABEL) {
            return "\n" + res + ":";
        } else if (op == Operator.LOAD) {
            String str = String.format("\t%s: %s = %s", res, type, main);
            if (supl != null) {
                str += String.format("[%s]", supl);
            }
            return str;
        } else if (op == Operator.STORE) {
            if (supl != null) {
                return String.format("\t%s[%s]: %s = %s", res, supl, type, main);
            } else {
                return String.format("\t%s: %s = %s", res, type, main);
            }
        } else if (op == Operator.GOTO) {
            return "\tgoto  " + res;
        } else if (op == Operator.GOIF || op == Operator.GONT) {
            String str;
            if (op == Operator.GOIF) {
                str = "\tif ";
            } else {
                str = "\tif not ";
            }
            str += String.format("%s goto  %s", main, res);
            return str;
        } else {
            StringBuilder sb = new StringBuilder();
            if (res != null) {
                sb.append(String.format("\t%s = %s", res.toString().toLowerCase(), op.toString().toLowerCase()));
            } else {
                sb.append(String.format("\t%s", op.toString().toLowerCase()));
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

        // Procedural
        FUNC, // function def
        PARAM, // push param
        CALL,
        RET,
    }
}
