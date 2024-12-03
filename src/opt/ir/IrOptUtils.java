package opt.ir;

import ir.datastruct.Instr;
import ir.datastruct.operand.Const;
import ir.datastruct.operand.FuncRef;
import ir.datastruct.operand.Var;

import java.util.ArrayList;

import static ir.datastruct.Instr.Type.*;

public class IrOptUtils {
    // Configs
    final static boolean genBlockExCounter = false;
    public static ArrayList<Var> blockProfilerVars = new ArrayList<>();

    public static ArrayList<Instr> cPrint(String s) {
        ArrayList<Instr> instrs = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            instrs.add(Instr.genParam(new Const(c), i8));
            instrs.add(Instr.genFuncCall(FuncRef.frPutchar()));
        }
        return instrs;
    }

    public static ArrayList<Instr> cPrintln(String s) {
        ArrayList<Instr> instrs = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            instrs.add(Instr.genParam(new Const(c), i8));
            instrs.add(Instr.genFuncCall(FuncRef.frPutchar()));
        }
        instrs.add(Instr.genParam(new Const('\n'), i8));
        instrs.add(Instr.genFuncCall(FuncRef.frPutchar()));
        return instrs;
    }

    public static ArrayList<Instr> cPrint(Var var) {
        ArrayList<Instr> instrs = new ArrayList<>();
        instrs.add(Instr.genParam(var, var.type));
        instrs.add(Instr.genFuncCall(var.type == i32 ? FuncRef.frPutint() : FuncRef.frPutchar()));
        return instrs;
    }

    public static ArrayList<Instr> cPrintln(Var var) {
        ArrayList<Instr> instrs = new ArrayList<>();
        instrs.add(Instr.genParam(var, var.type));
        instrs.add(Instr.genFuncCall(var.type == i32 ? FuncRef.frPutint() : FuncRef.frPutchar()));
        instrs.add(Instr.genParam(new Const('\n'), i8));
        instrs.add(Instr.genFuncCall(FuncRef.frPutchar()));
        return instrs;
    }
}
