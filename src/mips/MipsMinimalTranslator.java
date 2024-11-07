package mips;

import ir.datastruct.Instr;
import ir.datastruct.Ir;
import ir.datastruct.operand.*;
import mips.datastruct.MipsInstr;
import mips.datastruct.MipsProgram;
import mips.datastruct.MipsReg;

import java.util.ArrayList;
import java.util.HashMap;

import static mips.datastruct.MipsInstr.MipsOperator.*;
import static mips.datastruct.MipsReg.RegId.*;
import static mips.datastruct.MipsReg.r;

public class MipsMinimalTranslator implements MipsTranslator {
    // From:
    private final ArrayList<Instr> irInstrs;
    // To:
    private final MipsProgram program = new MipsProgram();
    private final HashMap<Reg, Const> tmpRegAddr = new HashMap<>();
    private final HashMap<Var, Const> varAddr = new HashMap<>();
    // Stack memory manager.
    // The VERY rough & simple stack manager allocate 4 bytes for all operands:
    // Vars & TempRegs.
    private int nextMem = 0; // Next location to allocate on stack frame.
    private boolean enterTextSeg = false;

    public MipsMinimalTranslator(Ir ir) {
        irInstrs = ir.genInstrs();
    }

    private void frameReset() {
        tmpRegAddr.clear();
        varAddr.clear();
        nextMem = 0;
    }

    private Const allocMem() {
        Const addr = new Const(nextMem);
        nextMem -= 4;
        program.append(MipsInstr.genCalc(ADDI, r(SP), r(SP), new Const(-4)));
        return addr;
    }

    private Const allocMem(Reg reg) {
        Const addr = new Const(nextMem);
        tmpRegAddr.put(reg, addr);
        nextMem -= 4;
        program.append(MipsInstr.genCalc(ADDI, r(SP), r(SP), new Const(-4)));
        return addr;
    }

    private Const allocMem(Var var) {
        Const addr = new Const(nextMem);
        varAddr.put(var, addr);
        nextMem -= 4;
        program.append(MipsInstr.genCalc(ADDI, r(SP), r(SP), new Const(-4)));
        return addr;
    }

    private Const offset(Reg reg) {
        return tmpRegAddr.get(reg);
    }

    private Const offset(Var var) {
        return varAddr.get(var);
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

    private void fromIrAdd(Instr irAdd) {
        load(irAdd.main, r(V0));
        if (irAdd.supl instanceof Const) {
            program.append(MipsInstr.genCalc(ADDI, r(V0), r(V0), (Const) irAdd.supl));
            store(irAdd.res, r(V0));
        } else {
            load(irAdd.supl, r(V1));
            program.append(MipsInstr.genCalc(ADDU, r(V0), r(V0), r(V1)));
            store(irAdd.res, r(V0));
        }
    }

    private void fromIrSub(Instr irSub) {
        load(irSub.main, r(V0));
        if (irSub.supl instanceof Const) {
            program.append(MipsInstr.genCalc(SUBI, r(V0), r(V0), (Const) irSub.supl));
            store(irSub.res, r(V0));
        } else {
            load(irSub.supl, r(V1));
            program.append(MipsInstr.genCalc(SUBU, r(V0), r(V0), r(V1)));
            store(irSub.res, r(V0));
        }
    }

    private void fromIrMul(Instr irMul) {
        load(irMul.main, r(V0));
        load(irMul.supl, r(V1));
        program.append(MipsInstr.genCalc(MUL, r(V0), r(V0), r(V1)));
        store(irMul.res, r(V0));
    }

    private void fromIrDiv(Instr irDiv) {
        load(irDiv.main, r(V0));
        load(irDiv.supl, r(V1));
        program.append(MipsInstr.genCalc(DIVU, r(V0), r(V0), r(V1)));
        program.append(MipsInstr.genMoveFrom(MFLO, r(V0)));
        store(irDiv.res, r(V0));
    }

    private void fromIrMod(Instr irMod) {
        load(irMod.main, r(V0));
        load(irMod.supl, r(V1));
        program.append(MipsInstr.genCalc(DIVU, r(V0), r(V0), r(V1)));
        program.append(MipsInstr.genMoveFrom(MFHI, r(V0)));
        store(irMod.res, r(V0));
    }

    private void fromIrMove(Instr irMove) {
        if (irMove.main instanceof Const c) {
            program.append(MipsInstr.genLi(r(V0), c));
        } else {
            load(irMove.main, r(V0));
            store(irMove.res, r(V0));
        }
    }

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
        frameReset();

        if (!enterTextSeg) {
            enterTextSeg = true;
            program.append(MipsInstr.genTextSeg());
        }
        program.append(MipsInstr.genLabel((FuncRef) irFunc.res));

        // Generate function head.
        program.append(MipsInstr.genMem(SW, r(FP), r(SP), new Const(0)));
        program.append(MipsInstr.genMove(r(FP), r(SP)));
        allocMem();
    }
    private void fromIrParam(Instr irParam) {}
    private void fromIrCall(Instr irCall) {}
    private void fromIrRet(Instr irRet) {
        if (irRet.main != null) {
            load(irRet.main, r(V0));
        }
        // Generate function tail.
        program.append(MipsInstr.genMove(r(SP), r(FP)));
        program.append(MipsInstr.genMem(LW, r(FP), r(SP), new Const(0)));
        program.append(MipsInstr.genJumpReg(r(RA)));
    }

    private void load(Operand operand, MipsReg reg) {
        if (operand instanceof Reg) {
            // Load from corresponding memory location.
            program.append(MipsInstr.genMem(LW, reg, r(FP), offset((Reg) operand)));
        } else if (operand instanceof Const c) {
            // Load imm.
            program.append(MipsInstr.genLi(reg, c));
        }
    }

    private void store(Operand to, MipsReg from) {
        if (to instanceof Reg tmpReg) {
            if (tmpRegAddr.containsKey(tmpReg)) {
                program.append(MipsInstr.genMem(SW, from, r(FP), offset(tmpReg)));
            } else {
                program.append(MipsInstr.genMem(SW, from, r(FP), allocMem(tmpReg)));
            }
        } else if (to instanceof Var var) {
            if (varAddr.containsKey(var)) {
                program.append(MipsInstr.genMem(SW, from, r(FP), offset(var)));
            } else {
                program.append(MipsInstr.genMem(SW, from, r(FP), allocMem(var)));
            }
        } else {
            assert false;
        }
    }
}
