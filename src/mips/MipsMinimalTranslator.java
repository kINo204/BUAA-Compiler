package mips;

import ir.datastruct.Instr;
import ir.datastruct.Ir;
import ir.datastruct.operand.*;
import mips.datastruct.MipsInstr;
import mips.datastruct.MipsOperand;
import mips.datastruct.MipsProgram;
import mips.datastruct.MipsReg;

import java.util.ArrayList;
import java.util.HashMap;

import static mips.datastruct.MipsReg.RegId.*;

public class MipsMinimalTranslator implements MipsTranslator {
    // From:
    private final ArrayList<Instr> irInstrs;
    // To:
    private final MipsProgram program = new MipsProgram();

    // Stack memory manager.
    // The VERY rough & simple stack manager allocate 4 bytes for all operands:
    // Vars & TempRegs.
    private int nextMem = 0; // Next location to allocate on stack frame.
    private final HashMap<Reg, Const> tmpRegAddr = new HashMap<>();
    private final HashMap<Var, Const> varAddr = new HashMap<>();

    private void frameReset() {
        tmpRegAddr.clear();
        varAddr.clear();
        nextMem = 0;
    }

    private void allocMem(Reg reg) {
        tmpRegAddr.put(reg, new Const(nextMem));
        nextMem -= 4;
    }

    private void allocMem(Var var) {
        varAddr.put(var, new Const(nextMem));
        nextMem -= 4;
    }

    private Const addressOf(Reg reg) {
        return tmpRegAddr.get(reg);
    }

    private Const addressOf(Var var) {
        return varAddr.get(var);
    }

    public MipsMinimalTranslator(Ir ir) {
        irInstrs = ir.genInstrs();
    }

    // Instruction dispatcher.
    @Override
    public MipsProgram translate() {
        for (Instr irInstr : irInstrs) {
            switch (irInstr.getOperator()) {
                case ALLOC -> fromIrAlloc(irInstr);
                case LOAD -> fromIrLoad(irInstr);
                case STORE -> fromIrStore(irInstr);
                case ADD -> fromIrAdd(irInstr);
                case SUB -> fromIrSub(irInstr);
                case MUL -> fromIrMul(irInstr);
                case DIV -> fromIrDiv(irInstr);
                case MOD -> fromIrMod(irInstr);
                case OR -> fromIrOr(irInstr);
                case AND -> fromIrAnd(irInstr);
                case MOVE -> fromIrMove(irInstr);
                case LSS -> fromIrLss(irInstr);
                case LEQ -> fromIrLeq(irInstr);
                case GRE -> fromIrGre(irInstr);
                case GEQ -> fromIrGeq(irInstr);
                case EQL -> fromIrEql(irInstr);
                case NEQ -> fromIrNeq(irInstr);
                case LABEL -> fromIrLabel(irInstr);
                case GOTO -> fromIrGoto(irInstr);
                case GOIF -> fromIrGoif(irInstr);
                case GONT -> fromIrGont(irInstr);
                case FUNC -> fromIrFunc(irInstr);
                case PARAM -> fromIrParam(irInstr);
                case CALL -> fromIrCall(irInstr);
                case RET -> fromIrRet(irInstr);
            }
        }
        return program;
    }

    private void fromIrAlloc(Instr irAlloc) {}
    private void fromIrLoad(Instr irLoad) {}
    private void fromIrStore(Instr irStore) {}
    private void fromIrAdd(Instr irAdd) {}
    private void fromIrSub(Instr irSub) {}
    private void fromIrMul(Instr irMul) {}
    private void fromIrDiv(Instr irDiv) {}
    private void fromIrMod(Instr irMod) {}
    private void fromIrOr(Instr irOr) {}
    private void fromIrAnd(Instr irAnd) {}
    private void fromIrMove(Instr irMove) {}
    private void fromIrLss(Instr irLss) {}
    private void fromIrLeq(Instr irLeq) {}
    private void fromIrGre(Instr irGre) {}
    private void fromIrGeq(Instr irGeq) {}
    private void fromIrEql(Instr irEql) {}
    private void fromIrNeq(Instr irNeq) {}
    private void fromIrLabel(Instr irLabel) {}
    private void fromIrGoto(Instr irGoto) {}
    private void fromIrGoif(Instr irGoif) {}
    private void fromIrGont(Instr irGont) {}
    private void fromIrFunc(Instr irFunc) {
        program.append(MipsInstr.genLabel((FuncRef) irFunc.res));
    }
    private void fromIrParam(Instr irParam) {}
    private void fromIrCall(Instr irCall) {}
    private void fromIrRet(Instr irRet) {
        if (irRet.main != null) {
            prepare(irRet.main, MipsReg.r(V0));
        }
        program.append(MipsInstr.genJumpReg(MipsReg.r(RA)));
    }

    private void prepare(Operand operand, MipsReg reg) {
        if (operand instanceof Reg) {
            // Load from corresponding memory location.
            program.append(MipsInstr.genMem(MipsInstr.MipsOperator.LW, reg, MipsReg.r(FP),
                    addressOf((Reg) operand)));
        } else if (operand instanceof Const c) {
            // Load imm.
            program.append(MipsInstr.genLi(reg, c));
        }
    }

    private void store() {

    }
}
