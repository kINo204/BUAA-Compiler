package mips;

import ir.datastruct.Instr;
import ir.datastruct.Ir;
import ir.datastruct.operand.*;
import mips.datastruct.MipsInstr;
import mips.datastruct.MipsProgram;
import mips.datastruct.MipsReg;

import java.util.ArrayList;
import java.util.HashMap;

import static ir.datastruct.Instr.Type.i8;
import static mips.datastruct.MipsInstr.MipsOperator.*;
import static mips.datastruct.MipsReg.RegId.*;
import static mips.datastruct.MipsReg.r;

public class MipsMinimalTranslator implements MipsTranslator {
    // From:
    private final ArrayList<Instr> irInstrs;
    // To:
    private final MipsProgram program = new MipsProgram();
    private boolean enteredTextSeg = false;


    // A VERY SIMPLE Stack memory manager.
    // The VERY rough & simple stack manager allocate 4 bytes for all operands
    // Vars & TempRegs.
    private int nextMem = 0; // Next location to allocate on stack frame. Always negative.
    private final HashMap<Reg, Const> tmpRegAddr = new HashMap<>();
    private final HashMap<Var, Const> varAddr = new HashMap<>();

    public MipsMinimalTranslator(Ir ir) {
        irInstrs = ir.genInstrs();
    }

    private void frameReset() {
        tmpRegAddr.clear();
        varAddr.clear();
        nextMem = 0;
    }

    private void alignMem(int align) {
        int prominent = (-nextMem) % align;
        nextMem -= prominent == 0 ? 0 : align - prominent;
    }

    private void allocWord() {
        alignMem(4);
        nextMem -= 4;
        program.append(MipsInstr.genCalc(addi, r(SP), r(SP), new Const(-4)));
    }

    private Const allocMem(Reg reg) {
        alignMem(reg.type.size());
        int memRequired = reg.type.size();
        nextMem -= memRequired;
        Const addr = new Const(nextMem);
        tmpRegAddr.put(reg, addr);

        program.append(MipsInstr.genCalc(addi, r(SP), r(SP), new Const(-memRequired)));
        return addr;
    }

    private Const allocMem(Var var) {
        alignMem(var.type.size());

        int memRequired = var.type.size();
        if (var.isArray) {
            memRequired *= var.arrayLength;
        }

        nextMem -= memRequired;
        Const addr = new Const(nextMem);
        varAddr.put(var, addr);

        program.append(MipsInstr.genCalc(addi, r(SP), r(SP), new Const(-memRequired)));
        return addr;
    }

    private Const offset(Reg reg) {
        return tmpRegAddr.get(reg);
    }

    private Const offset(Var var) {
        return varAddr.get(var);
    }

    private void load(Operand operand, MipsReg reg) {
        if (operand instanceof Var var) {
            // Load from corresponding memory location.
            assert !var.isArray;
            program.append(MipsInstr.genMem(var.type == i8 ? lb : lw, reg, r(FP), offset(var)));
        } else if (operand instanceof Reg) {
            // Load from corresponding memory location.
            program.append(MipsInstr.genMem(operand.type == i8 ? lb : lw, reg, r(FP), offset((Reg) operand)));
        } else if (operand instanceof Const c) {
            // Load imm.
            program.append(MipsInstr.genLi(reg, c));
        }
    }

    private void loadArray(Operand operand, int index, MipsReg reg) {
        assert operand instanceof Var;
        Var var = (Var) operand;
        assert var.isArray;
        assert index < var.arrayLength;

        int ofs = offset(var).num - index * var.type.size();
        program.append(MipsInstr.genMem(var.type == i8 ? lb : lw, reg, r(FP), new Const(ofs)));
    }

    private void store(Operand to, MipsReg from) {
        if (to instanceof Reg tmpReg) {
            if (tmpRegAddr.containsKey(tmpReg)) {
                program.append(MipsInstr.genMem(to.type == i8 ? sb : sw, from, r(FP), offset(tmpReg)));
            } else {
                program.append(MipsInstr.genMem(to.type == i8 ? sb : sw, from, r(FP), allocMem(tmpReg)));
            }
        } else if (to instanceof Var var) {
            assert !var.isArray;
            if (varAddr.containsKey(var)) {
                program.append(MipsInstr.genMem(to.type == i8 ? sb : sw, from, r(FP), offset(var)));
            } else {
                // Allocate memory.
                program.append(MipsInstr.genMem(to.type == i8 ? sb : sw, from, r(FP), allocMem(var)));
            }
        } else {
            assert false;
        }
    }

    private void storeArray(Operand to, int index, MipsReg from) {
        assert to instanceof Var;
        Var var = (Var) to;
        assert var.isArray;
        assert index < var.arrayLength;

        int ofs;
        if (varAddr.containsKey(var)) {
            ofs = offset(var).num - index * var.type.size();
        } else {
            // Allocate memory.
            ofs = allocMem(var).num - index * var.type.size();
        }
        program.append(MipsInstr.genMem(to.type == i8 ? sb : sw, from, r(FP), new Const(ofs)));
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

    private void fromIrAlloc(Instr irAlloc) {
        // Do nothing yet.
    }

    private void fromIrLoad(Instr irLoad) {
        assert irLoad.main instanceof Var;
        Var varMain = (Var) irLoad.main;
        if (varMain.isArray) {
            if (irLoad.supl == null) {
                loadArray(irLoad.main, 0, r(V0));
            } else {
                loadArray(irLoad.main, ((Const) irLoad.supl).num, r(V0));
            }
        } else {
            assert irLoad.supl == null;
            load(irLoad.main, r(V0));
        }

        assert irLoad.res instanceof Reg;
        store(irLoad.res, r(V0));
    }

    private void fromIrStore(Instr irStore) {
        if (irStore.main instanceof Var varMain) {
            if (varMain.isArray) {
                if (irStore.supl == null) {
                    loadArray(irStore.main, 0, r(V0));
                } else {
                    loadArray(irStore.main, ((Const) irStore.supl).num, r(V0));
                }
            } else {
                assert irStore.supl == null;
                load(irStore.main, r(V0));
            }
        } else { // Const, Reg
            load(irStore.main, r(V0));
        }

        assert irStore.res instanceof Var;
        Var varRes = (Var) irStore.res;
        if (varRes.isArray) {
            if (irStore.supl == null) {
                storeArray(irStore.res, 0, r(V0));
            } else {
                storeArray(irStore.res, ((Const) irStore.supl).num, r(V0));
            }
        } else {
            assert irStore.supl == null;
            store(irStore.res, r(V0));
        }
    }

    private void fromIrAdd(Instr irAdd) {
        load(irAdd.main, r(V0));
        if (irAdd.supl instanceof Const) {
            program.append(MipsInstr.genCalc(addi, r(V0), r(V0), (Const) irAdd.supl));
            store(irAdd.res, r(V0));
        } else {
            load(irAdd.supl, r(V1));
            program.append(MipsInstr.genCalc(addu, r(V0), r(V0), r(V1)));
            store(irAdd.res, r(V0));
        }
    }

    private void fromIrSub(Instr irSub) {
        load(irSub.main, r(V0));
        if (irSub.supl instanceof Const) {
            program.append(MipsInstr.genCalc(subi, r(V0), r(V0), (Const) irSub.supl));
            store(irSub.res, r(V0));
        } else {
            load(irSub.supl, r(V1));
            program.append(MipsInstr.genCalc(subu, r(V0), r(V0), r(V1)));
            store(irSub.res, r(V0));
        }
    }

    private void fromIrMul(Instr irMul) {
        load(irMul.main, r(V0));
        load(irMul.supl, r(V1));
        program.append(MipsInstr.genCalc(mul, r(V0), r(V0), r(V1)));
        store(irMul.res, r(V0));
    }

    private void fromIrDiv(Instr irDiv) {
        load(irDiv.main, r(V0));
        load(irDiv.supl, r(V1));
        program.append(MipsInstr.genCalc(divu, r(V0), r(V0), r(V1)));
        program.append(MipsInstr.genMoveFrom(mflo, r(V0)));
        store(irDiv.res, r(V0));
    }

    private void fromIrMod(Instr irMod) {
        load(irMod.main, r(V0));
        load(irMod.supl, r(V1));
        program.append(MipsInstr.genCalc(divu, r(V0), r(V0), r(V1)));
        program.append(MipsInstr.genMoveFrom(mfhi, r(V0)));
        store(irMod.res, r(V0));
    }

    private void fromIrMove(Instr irMove) {
        load(irMove.main, r(V0));
        store(irMove.res, r(V0));
    }

    private void fromIrLss(Instr irLss) {}

    private void fromIrLeq(Instr irLeq) {}

    private void fromIrGre(Instr irGre) {}

    private void fromIrGeq(Instr irGeq) {}

    private void fromIrEql(Instr irEql) {}

    private void fromIrNeq(Instr irNeq) {}

    // Label decl
    private void fromIrLabel(Instr irLabel) {
        program.append(MipsInstr.genLabel((Label) irLabel.res));
    }

    private void fromIrGoto(Instr irGoto) {
        program.append(MipsInstr.genJump((Label) irGoto.res));
    }

    private void fromIrGoif(Instr irGoif) {
        load(irGoif.main, r(V0));
        program.append(MipsInstr.genBne(r(V0), r(ZERO), (Label) irGoif.res));
    }

    private void fromIrGont(Instr irGont) {
        load(irGont.main, r(V0));
        program.append(MipsInstr.genBeq(r(V0), r(ZERO), (Label) irGont.res));
    }

    private void fromIrFunc(Instr irFunc) {
        frameReset();

        if (!enteredTextSeg) {
            enteredTextSeg = true;
            program.append(MipsInstr.genTextSeg());
        }
        program.append(MipsInstr.genLabel((FuncRef) irFunc.res));

        // Generate function head.
        program.append(MipsInstr.genMem(sw, r(FP), r(SP), new Const(0)));
        program.append(MipsInstr.genMove(r(FP), r(SP)));
        allocWord();
    }
    private void fromIrParam(Instr irParam) {
        // TODO
    }

    private void fromIrCall(Instr irCall) {
        // TODO
    }

    private void fromIrRet(Instr irRet) {
        if (irRet.main != null) {
            load(irRet.main, r(V0));
        }
        // Generate function tail.
        program.append(MipsInstr.genMove(r(SP), r(FP)));
        program.append(MipsInstr.genMem(lw, r(FP), r(SP), new Const(0)));
        program.append(MipsInstr.genJumpReg(r(RA)));
    }
}
