package mips;

import ir.datastruct.Instr;
import ir.datastruct.Ir;
import ir.datastruct.operand.*;
import mips.datastruct.MipsInstr;
import mips.datastruct.MipsProgram;
import mips.datastruct.MipsReg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ir.datastruct.Instr.Type.i32;
import static ir.datastruct.Instr.Type.i8;
import static mips.datastruct.MipsInstr.MipsOperator.*;
import static mips.datastruct.MipsReg.RegId.*;
import static mips.datastruct.MipsReg.r;

public class MipsMinimalTranslator implements MipsTranslator {
    private final boolean withIr = true;

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

    private boolean firstParam;
    private int ofsCall;
    private void frameReset() {
        tmpRegAddr.clear();
        varAddr.clear();

        firstParam = true;
        ofsCall = 0;

        nextMem = 0;
    }

    private void alignMem(int align) {
        int prominent = (-nextMem) % align;
        int alter = prominent == 0 ? 0 : align - prominent;
        nextMem -= alter;
        program.append(MipsInstr.genCalc(addi, r(sp), r(sp), new Const(-alter)));
    }

    private Const allocMem(int size, boolean genSpInstr) {
        alignMem(size);

        nextMem -= size;
        Const addr = new Const(nextMem);

        if (genSpInstr)
            program.append(MipsInstr.genCalc(addi, r(sp), r(sp), new Const(-size)));

        return addr;
    }

    private Const allocMem(Reg reg, boolean genSpInstr) {
        int size = reg.type.size();

        alignMem(size);

        nextMem -= size;
        Const addr = new Const(nextMem); // Base addr is after stack push!
        tmpRegAddr.put(reg, addr);

        if (genSpInstr)
            program.append(MipsInstr.genCalc(addi, r(sp), r(sp), new Const(-size)));

        return addr;
    }

    private Const allocMem(Var var, boolean genSpInstr) {
        int size = var.isReference ? 4 : var.type.size();

        alignMem(size);
        if (var.isArray) {
            assert !var.isReference;
            size *= var.arrayLength;
        }
        nextMem -= size;

        Const addr = new Const(nextMem);
        varAddr.put(var, addr);

        if (genSpInstr)
            program.append(MipsInstr.genCalc(addi, r(sp), r(sp), new Const(-size)));

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
            if (var.isReference) {
                program.append(MipsInstr.genMem(lw, reg, r(fp), offset(var)));
            } else {
                program.append(MipsInstr.genMem(var.type == i8 ? lb : lw, reg, r(fp), offset(var)));
            }
        } else if (operand instanceof Reg) {
            // Load from corresponding memory location.
            program.append(MipsInstr.genMem(operand.type == i8 ? lb : lw, reg, r(fp), offset((Reg) operand)));
        } else if (operand instanceof Const c) {
            // Load imm.
            program.append(MipsInstr.genLi(reg, c));
        }
    }

    private void loadArray(Operand operand, int index, MipsReg reg) {
        assert operand instanceof Var;
        Var var = (Var) operand;
        assert var.isArray;
        assert var.arrayLength < 0 || index < var.arrayLength;

        int ofs = offset(var).num + index * var.type.size();
        program.append(MipsInstr.genMem(var.type == i8 ? lb : lw, reg, r(fp), new Const(ofs)));
    }

    private void store(Operand to, MipsReg from) {
        if (to instanceof Reg tmpReg) {
            if (tmpRegAddr.containsKey(tmpReg)) {
                program.append(MipsInstr.genMem(to.type == i8 ? sb : sw, from, r(fp), offset(tmpReg)));
            } else {
                program.append(MipsInstr.genMem(to.type == i8 ? sb : sw, from, r(fp), allocMem(tmpReg, true)));
            }
        } else if (to instanceof Var var) {
            assert !var.isArray;
            Instr.Type type = var.isReference ? i32 : to.type;
            if (varAddr.containsKey(var)) {
                program.append(MipsInstr.genMem(type == i8 ? sb : sw, from, r(fp), offset(var)));
            } else {
                // Allocate memory.
                program.append(MipsInstr.genMem(type == i8 ? sb : sw, from, r(fp), allocMem(var, true)));
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
            ofs = offset(var).num + index * var.type.size();
        } else {
            // Allocate memory.
            ofs = allocMem(var, true).num + index * var.type.size();
        }
        program.append(MipsInstr.genMem(to.type == i8 ? sb : sw, from, r(fp), new Const(ofs)));
    }

    // Instruction dispatcher.
    @Override
    public MipsProgram translate() {
        for (Instr irInstr : irInstrs) {
            if (withIr) {
                program.append(MipsInstr.genComment(irInstr.toString()));
            }
            switch (irInstr.getOperator()) {
                case ALLOC -> fromIrAlloc(irInstr);
                case LOAD -> fromIrLoadArr(irInstr);
                case STORE -> fromIrStoreArr(irInstr);
                case ADDR -> fromIrGetAddress(irInstr);
                case DEREF -> fromIrDerefAndIndex(irInstr);
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

    private void fromIrLoadArr(Instr irLoad) {
        assert irLoad.main instanceof Var;
        Var varMain = (Var) irLoad.main;
        if (varMain.isArray) {
            if (irLoad.supl == null) {
                loadArray(irLoad.main, 0, r(v0));
            } else {
                loadArray(irLoad.main, ((Const) irLoad.supl).num, r(v0));
            }
        } else {
            assert irLoad.supl == null;
            load(irLoad.main, r(v0));
        }

        assert irLoad.res instanceof Reg;
        store(irLoad.res, r(v0));
    }

    private void fromIrStoreArr(Instr irStore) {
        if (irStore.main instanceof Var varMain) {
            if (varMain.isArray) {
                if (irStore.supl == null) {
                    loadArray(irStore.main, 0, r(v0));
                } else {
                    loadArray(irStore.main, ((Const) irStore.supl).num, r(v0));
                }
            } else {
                assert irStore.supl == null;
                load(irStore.main, r(v0));
            }
        } else { // Const, Reg
            load(irStore.main, r(v0));
        }

        assert irStore.res instanceof Var;
        Var varRes = (Var) irStore.res;
        if (varRes.isArray) {
            if (irStore.supl == null) {
                storeArray(irStore.res, 0, r(v0));
            } else {
                storeArray(irStore.res, ((Const) irStore.supl).num, r(v0));
            }
        } else {
            assert irStore.supl == null;
            store(irStore.res, r(v0));
        }
    }

    private void fromIrGetAddress(Instr irGetAddr) {
        assert irGetAddr.main instanceof Var;
        program.append(MipsInstr.genCalc(addi, r(v0), r(fp), varAddr.get((Var) irGetAddr.main)));
        store(irGetAddr.res, r(v0));
    }

    private void fromIrDerefAndIndex(Instr irDeref) {
        load(irDeref.main, r(a0)); // Address to deref.
        int ofs = ((Const) irDeref.supl).num * irDeref.type.size();
        program.append(MipsInstr.genMem(irDeref.type == i8 ? lb : lw, r(v0), r(a0), new Const(ofs)));
        store(irDeref.res, r(v0));
    }

    private void fromIrAdd(Instr irAdd) {
        load(irAdd.main, r(v0));
        if (irAdd.supl instanceof Const) {
            program.append(MipsInstr.genCalc(addi, r(v0), r(v0), (Const) irAdd.supl));
            store(irAdd.res, r(v0));
        } else {
            load(irAdd.supl, r(v1));
            program.append(MipsInstr.genCalc(addu, r(v0), r(v0), r(v1)));
            store(irAdd.res, r(v0));
        }
    }

    private void fromIrSub(Instr irSub) {
        load(irSub.main, r(v0));
        if (irSub.supl instanceof Const) {
            program.append(MipsInstr.genCalc(subi, r(v0), r(v0), (Const) irSub.supl));
            store(irSub.res, r(v0));
        } else {
            load(irSub.supl, r(v1));
            program.append(MipsInstr.genCalc(subu, r(v0), r(v0), r(v1)));
            store(irSub.res, r(v0));
        }
    }

    private void fromIrMul(Instr irMul) {
        load(irMul.main, r(v0));
        load(irMul.supl, r(v1));
        program.append(MipsInstr.genCalc(mul, r(v0), r(v0), r(v1)));
        store(irMul.res, r(v0));
    }

    private void fromIrDiv(Instr irDiv) {
        load(irDiv.main, r(v0));
        load(irDiv.supl, r(v1));
        program.append(MipsInstr.genCalc(divu, r(v0), r(v0), r(v1)));
        program.append(MipsInstr.genMoveFrom(mflo, r(v0)));
        store(irDiv.res, r(v0));
    }

    private void fromIrMod(Instr irMod) {
        load(irMod.main, r(v0));
        load(irMod.supl, r(v1));
        program.append(MipsInstr.genCalc(divu, r(v0), r(v0), r(v1)));
        program.append(MipsInstr.genMoveFrom(mfhi, r(v0)));
        store(irMod.res, r(v0));
    }

    private void fromIrMove(Instr irMove) {
        load(irMove.main, r(v0));
        store(irMove.res, r(v0));
    }

    private void fromIrLss(Instr irLss) {
        load(irLss.main, r(v0));
        load(irLss.supl, r(v1));
        program.append(MipsInstr.genCalc(slt, r(v0), r(v0), r(v1)));
        store(irLss.res, r(v0));
    }

    private void fromIrLeq(Instr irLeq) {
        load(irLeq.main, r(v0));
        load(irLeq.supl, r(v1));
        program.append(MipsInstr.genCalc(sle, r(v0), r(v0), r(v1)));
        store(irLeq.res, r(v0));
    }

    private void fromIrGre(Instr irGre) {
        load(irGre.main, r(v0));
        load(irGre.supl, r(v1));
        program.append(MipsInstr.genCalc(sgt, r(v0), r(v0), r(v1)));
        store(irGre.res, r(v0));
    }

    private void fromIrGeq(Instr irGeq) {
        load(irGeq.main, r(v0));
        load(irGeq.supl, r(v1));
        program.append(MipsInstr.genCalc(sge, r(v0), r(v0), r(v1)));
        store(irGeq.res, r(v0));
    }

    private void fromIrEql(Instr irEql) {
        load(irEql.main, r(v0));
        load(irEql.supl, r(v1));
        program.append(MipsInstr.genCalc(seq, r(v0), r(v0), r(v1)));
        store(irEql.res, r(v0));
    }

    private void fromIrNeq(Instr irNeq) {
        load(irNeq.main, r(v0));
        load(irNeq.supl, r(v1));
        program.append(MipsInstr.genCalc(sne, r(v0), r(v0), r(v1)));
        store(irNeq.res, r(v0));
    }

    // Label decl
    private void fromIrLabel(Instr irLabel) {
        program.append(MipsInstr.genLabel((Label) irLabel.res));
    }

    private void fromIrGoto(Instr irGoto) {
        program.append(MipsInstr.genJump((Label) irGoto.res));
    }

    private void fromIrGoif(Instr irGoif) {
        load(irGoif.main, r(v0));
        program.append(MipsInstr.genBne(r(v0), r(zero), (Label) irGoif.res));
    }

    private void fromIrGont(Instr irGont) {
        load(irGont.main, r(v0));
        program.append(MipsInstr.genBeq(r(v0), r(zero), (Label) irGont.res));
    }

    private void fromIrFunc(Instr irFunc) {
        frameReset(); // nextMem = 0

        // Prepare param entries.
        for (Var varParam : ((FuncRef) irFunc.res).params) {
            allocMem(varParam, false);
        }
        allocMem(4, false); // simulate memory for $ra
        int translateAmount = -nextMem;
        for (Map.Entry<Var, Const> entry : varAddr.entrySet()) {
            entry.setValue(new Const(entry.getValue().num + translateAmount));
        }
        // Now we have param entries, just reset nextMem again.
        nextMem = 0;

        if (!enteredTextSeg) {
            enteredTextSeg = true;
            program.append(MipsInstr.genTextSeg());
        }
        program.append(MipsInstr.genLabel((FuncRef) irFunc.res));

        // Generate function head.
        program.append(MipsInstr.genMem(sw, r(fp), r(sp), new Const(-4)));
        program.append(MipsInstr.genMove(r(fp), r(sp)));
        allocMem(4, true);
    }
    private void fromIrParam(Instr irParam) {
        if (firstParam) {
            firstParam = false;
            ofsCall = nextMem;

            alignMem(4); // To ensure same memory layout in caller & callee.
        }

        // Push param.
        load(irParam.main, r(a0));
        // Use "new Reg()" to force allocate new stack memory for parameter.
        // Reference is thrown away for there's no future use of real parameter.
        // For now, push all params on stack.
        store(new Reg(irParam.main.type), r(a0)); // No one will remember you since, ever.
    }

    private void fromIrCall(Instr irCall) {
        if (firstParam) { // If no param, falls here.
            firstParam = false;
            ofsCall = nextMem;

            alignMem(4); // To ensure predictable memory layout in caller & callee.
        }

        allocMem(4, true);
        program.append(MipsInstr.genMem(sw, r(ra), r(sp), new Const(0)));
        program.append(MipsInstr.genJal((FuncRef) irCall.main));
        program.append(MipsInstr.genMem(lw, r(ra), r(sp), new Const(0)));

        if (irCall.res != null) {
            store(irCall.res, r(v0));
        }

        // Recover param status.
        program.append(MipsInstr.genCalc(addi, r(sp), r(fp), new Const(ofsCall)));
        firstParam = true;
        nextMem = ofsCall;
    }

    private void fromIrRet(Instr irRet) {
        if (irRet.main != null) {
            load(irRet.main, r(v0));
        }

        // Generate function tail.
        program.append(MipsInstr.genMove(r(sp), r(fp)));
        program.append(MipsInstr.genMem(lw, r(fp), r(sp), new Const(-4)));
        program.append(MipsInstr.genJumpReg(r(ra)));
    }
}