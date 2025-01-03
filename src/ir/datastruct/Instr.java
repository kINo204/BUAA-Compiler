package ir.datastruct;

import ir.datastruct.operand.*;

public class Instr {
    public Operator op;
    public Type type = null;
    public Operand res = null, main = null, supl = null;

    private Instr(Operator op) {
        this.op = op;
    }

    private Instr(Operator operator, Type type, Operand res, Operand main, Operand supl) {
        this.op = operator;
        this.type = type;
        this.res = res;
        this.main = main;
        this.supl = supl;
    }

    @Deprecated
    public static Instr genRemStack() {
        return new Instr(Operator.REM_STACK);
    }

    @Deprecated
    public static Instr genLoadStack() {
        return new Instr(Operator.LOD_STACK);
    }

    @Deprecated
    public static Instr genPopStack() {
        return new Instr(Operator.POP_STACK);
    }

    @Deprecated
    public static Instr genUseStub(Var var) {
        return new Instr(Operator.USE, null, null, var, null);
    }

    public static Instr genGlobDecl(Var var, GlobInitVals initVals) {
        return new Instr(
                Operator.GLOB, var.type, var,
                !var.isArray ? null : new Const(var.arrayLength),
                initVals
        );
    }

    public static Instr genFuncDef(FuncRef funcRef) {
        return new Instr(
                Operator.FUNC, funcRef.type, funcRef, null, null
        );
    }

    public static Instr genParam(Operand param, Type type) {
        return new Instr(
                Operator.PARAM, type, null, param, null
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
        if (var.isArray) {
            return new Instr( Operator.ALLOC, var.type, var, new Const(var.arrayLength), null );
        } else {
            return new Instr( Operator.ALLOC, var.type, var, null, null );
        }
    }

    @Deprecated
    public static Instr genLoad(Reg reg, Var var) {
        return new Instr(
                Operator.LDARR, reg.type, reg, var, null
        );
    }

    @Deprecated
    public static Instr genStore(Var var, Operand value) {
        return new Instr(
                Operator.STARR, var.type, var, value, null
        );
    }

    public static Instr genLoadArr(Reg reg, Var var, Operand arrayIndex) {
        return new Instr(
                Operator.LDARR, reg.type, reg, var, arrayIndex
        );
    }

    public static Instr genStoreArr(Var var, Operand value, Operand arrayIndex) {
        return new Instr(
                Operator.STARR, var.type, var, value, arrayIndex
        );
    }

    public static Instr genLoadRef(Reg res, Var var, Operand arrayIndex) {
        return new Instr(
                Operator.LDREF, var.type, res, var, arrayIndex
        );
    }

    public static Instr genStoreRef(Var var, Operand data, Operand arrayIndex) {
        return new Instr(
                Operator.STREF, var.type, var, data, arrayIndex
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

    public static Instr genMove(Operand to, Operand from) {
        return new Instr(
                Operator.MOVE, to.type, to, from, null
        );
    }

    public static Instr genGetAddr(Operand res, Var var) {
        return new Instr(
                Operator.ADDR, Type.i32, res, var, null
        );
    }

    @Override
    public String toString() {
        if (op == Operator.GLOB) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("global %s", res));
            if (main != null) {
                sb.append("[").append(main).append("]");
            }
            sb.append(String.format(": %s = %s", type, supl));
            return sb.toString();
        } else if (op == Operator.FUNC) {
            return "define fun " + res + ":";
        } else if (op == Operator.MOVE) {
            return "\t" + res + ": " + type + " = " + main;
        } else if (op == Operator.LABEL) {
            return "\n" + res + ":";
        } else if (op == Operator.LDARR) {
            String str = String.format("\t%s: %s = %s", res, type, main);
            if (supl != null) {
                str += String.format("[%s]", supl);
            }
            return str;
        } else if (op == Operator.STARR) {
            if (supl != null) {
                return String.format("\t%s[%s]: %s = %s", res, supl, type, main);
            } else {
                return String.format("\t%s: %s = %s", res, type, main);
            }
        } else if (op == Operator.ADDR) {
            return String.format("\t%s: &%s = &(%s)", res, type, main);
        } else if (op == Operator.LDREF) {
            return String.format("\t%s: %s = *(%s)[%s]", res, type, main, supl);
        } else if (op == Operator.STREF) {
            return String.format("\t*(%s)[%s]: %s = %s", res, supl, type, main);
        } else if (op == Operator.GOTO) {
            return "\tgoto  " + res;
        } else if (op == Operator.GOIF || op == Operator.GONT) {
            String str;
            if (op == Operator.GOIF) {
                str = "\tif ";
            } else {
                str = "\tif false ";
            }
            str += String.format("%s goto  %s", main, res);
            return str;
        } else if (op == Operator.REM_STACK) {
            return "\tstack rem";
        } else if (op == Operator.LOD_STACK) {
            return "\tstack lod";
        } else if (op == Operator.POP_STACK) {
            return "\tstack pop";
        } else if (op == Operator.ADD) {
            return String.format("\t%s: %s = %s %s %s", res, type, main, "+", supl);
        } else if (op == Operator.SUB) {
            return String.format("\t%s: %s = %s %s %s", res, type, main, "-", supl);
        } else if (op == Operator.MUL) {
            return String.format("\t%s: %s = %s %s %s", res, type, main, "*", supl);
        } else if (op == Operator.DIV) {
            return String.format("\t%s: %s = %s %s %s", res, type, main, "/", supl);
        } else if (op == Operator.MOD) {
            return String.format("\t%s: %s = %s %s %s", res, type, main, "%", supl);
        } else if (op == Operator.OR) {
            return String.format("\t%s: %s = %s %s %s", res, type, main, "||", supl);
        } else if (op == Operator.AND) {
            return String.format("\t%s: %s = %s %s %s", res, type, main, "&&", supl);
        } else if (op == Operator.GRE) {
            return String.format("\t%s: %s = %s %s %s", res, type, main, ">", supl);
        } else if (op == Operator.GEQ) {
            return String.format("\t%s: %s = %s %s %s", res, type, main, ">=", supl);
        } else if (op == Operator.LSS) {
            return String.format("\t%s: %s = %s %s %s", res, type, main, "<", supl);
        } else if (op == Operator.LEQ) {
            return String.format("\t%s: %s = %s %s %s", res, type, main, "<=", supl);
        } else if (op == Operator.EQL) {
            return String.format("\t%s: %s = %s %s %s", res, type, main, "==", supl);
        } else if (op == Operator.NEQ) {
            return String.format("\t%s: %s = %s %s %s", res, type, main, "!=", supl);
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
        GLOB, ALLOC,
        LDARR, STARR,
        ADDR,
        LDREF, STREF,
        REM_STACK, LOD_STACK, POP_STACK,

        // Arithmetic
        MOVE,
        ADD, SUB, MUL, DIV, MOD,
        OR, AND,

        // Comparison
        LSS, LEQ, GRE, GEQ, EQL, NEQ,

        // Jumping
        LABEL, GOTO, GOIF, GONT,

        // Procedural
        FUNC, PARAM, CALL, RET,

        // Dataflow Stubs
        USE,
        DEF,
    }
}
