package ir.datastruct;

import ir.datastruct.operand.*;

public class Instr {
    private final Operator op;
    public Type type = null;
    public Operand res = null, main = null, supl = null;

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

    public static Instr genParam(Operand param) {
        return new Instr(
                Operator.PARAM, null, null, param, null
        );
    }

    public static Instr genFuncCall(Operand res, FuncRef funcRef) {
        return new Instr(
                Operator.CALL, res.type, res, funcRef, null
        );
    }

    public static Instr genFuncCall(FuncRef funcRef) {
        return new Instr(
                Operator.CALL, Type.VOID, null, funcRef, null
        );
    }

    public static Instr genAlloc(Var var) {
        return new Instr(
                Operator.ALLOC, var.type, var, null, null
        );
    }

    public static Instr genAlloc(Var var, Operand arrayLength) {
        return new Instr(
                Operator.ALLOC, var.type, var, arrayLength, null
        );
    }

    public static Instr genLoad(Reg reg, Var var) {
        return new Instr(
                Operator.LOAD, reg.type, reg, var, null
        );
    }

    public static Instr genStore(Var var, Operand value) {
        return new Instr(
                Operator.STORE, var.type, var, value, null
        );
    }

    public static Instr genLoad(Reg reg, Var var, Operand arrayIndex) {
        return new Instr(
                Operator.LOAD, reg.type, reg, var, arrayIndex
        );
    }

    public static Instr genStore(Var var, Operand value, Operand arrayIndex) {
        return new Instr(
                Operator.STORE, var.type, var, value, arrayIndex
        );
    }


    public static Instr genGoto(Label label) {
        return new Instr(
                Operator.GOTO, null, label, null, null
        );
    }

    public static Instr genGoif(Label label, Operand condValue) {
        return new Instr(
                Operator.GOIF, null, label, condValue, null
        );
    }

    public static Instr genGoIfNot(Label label, Operand condValue) {
        return new Instr(
                Operator.GONT, null, label, condValue, null
        );
    }

    public static Instr genLabelDecl(Label label) {
        Instr instr = new Instr(
                Operator.LABEL, null, label, null, null
        );
        label.target = instr; // Set target of the label to this instruction!!!
        return instr;
    }

    public static Instr genReturn() {
        return new Instr(Operator.RET);
    }

    public static Instr genReturn(Operand returnExp) {
        return new Instr(
                Operator.RET, returnExp.type, null, returnExp, null
        );
    }

    public static Instr genCalc(Operator op, Reg res, Operand main, Operand supl) {
        return new Instr(
                op, res.type, res, main, supl
        );
    }

    public static Instr genMove(Operand from, Operand to) {
        return new Instr(
                Operator.MOVE, to.type, to, from, null
        );
    }

    public static Instr genGetAddr(Operand res, Var var) {
        return new Instr(
                Operator.ADDR, Type.i32, res, var, null
        );
    }

    public static Instr genDeref(Reg res, Var var, Operand arrayIndex) {
        return new Instr(
                Operator.DEREF, var.type, res, var, arrayIndex
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
        } else if (op == Operator.ADDR) {
            return String.format("\t%s: &%s = &(%s)", res, type, main);
        } else if (op == Operator.DEREF) {
            return String.format("\t%s: %s = *(%s)[%s]", res, type, main, supl);
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

    public Operator getOperator() {
        return op;
    }

    public enum Type {
        i8(1), i32(4), VOID(0);

        private final int size;

        Type(int size) {
            this.size = size;
        }

        public int size() {
            return size;
        }
    }

    public enum Operator {
        // Memory
        ALLOC,
        LOAD,
        STORE,
        ADDR,
        DEREF,

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
