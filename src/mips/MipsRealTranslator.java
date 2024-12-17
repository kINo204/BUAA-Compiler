package mips;

import ir.datastruct.Instr;
import ir.datastruct.Ir;
import ir.datastruct.operand.*;
import mips.datastruct.MipsInstr;
import mips.datastruct.MipsProgram;
import mips.datastruct.MipsReg;
import opt.ir.datastruct.Unit;
import opt.ir.GlobalAlloc;

import java.util.*;

import static ir.datastruct.Instr.Operator.CALL;
import static ir.datastruct.Instr.Type.i32;
import static ir.datastruct.Instr.Type.i8;
import static mips.datastruct.MipsInstr.MipsOperator.*;
import static mips.datastruct.MipsInstr.MipsOperator.sw;
import static mips.datastruct.MipsReg.RegId.*;
import static mips.datastruct.MipsReg.r;

public class MipsRealTranslator implements MipsTranslator {
    /* An IR Translator follow the contract that, given a sequence
    of linear IR instructions, it may execute translating motions
    on each instruction, relatively independent on each other.
      Therefore, get the IR we're told to translate, and start
    working here.
     */
    public MipsRealTranslator(Ir ir, GlobalAlloc globalAlloc) { irInstrs = ir.genInstrs();
        this.globalAlloc = globalAlloc;
    }

    /* Data */
    private final ArrayList<Instr> irInstrs;
    private final MipsProgram program = new MipsProgram();

    /* Components */
    private final GlobalAlloc globalAlloc;
    private final RegsPool regsPool = new RegsPool();
    private final Stack stack = new Stack();

    /* Translators */
    @Override
    public MipsProgram translate() {
        for (Instr irInstr : irInstrs) {
            program.append(MipsInstr.genComment(irInstr.toString())); // Print IR instr.
            switch (irInstr.getOperator()) {
                case GLOB -> fromIrGlobDecl(irInstr);
                case ALLOC -> fromIrAlloc(irInstr);
                case LDARR -> fromIrLoad(irInstr);
                case STARR -> fromIrStore(irInstr);
                case ADDR -> fromIrGetAddress(irInstr);
                case LDREF -> fromIrDerefAndIndex(irInstr);
                case STREF -> fromIrStrefAndIndex(irInstr);
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

    private boolean enteredData = false;
    private void fromIrGlobDecl(Instr irInstr) {
        if (!enteredData) {
            enteredData = true;
            program.append(MipsInstr.genDataSeg());
        }
        program.append(MipsInstr.genGlobDecl((Var) irInstr.res, irInstr.type, (GlobInitVals) irInstr.supl));
    }

    private void fromIrAlloc(Instr irAlloc) {
        // We have to alloc here to avoid problem for single allocation
        // not performed in some control flow branches because it has been
        // allocated in a previous branch.
        /* Actually no need for this now, be we still keep this for convenience. */
        stack.allocMem((Var) irAlloc.res);
    }

    private void fromIrLoad(Instr irLoad) {
        Var varMain = (Var) irLoad.main;
        Unit uMain, uRes = new Unit(irLoad.res);

        if (!varMain.isArray) {
            uMain = new Unit(varMain);
        } else {
            uMain = irLoad.supl == null ?
                    new Unit(varMain, new Const(0)) :
                    new Unit(varMain, irLoad.supl);
        }
        copy(uMain, uRes);
    }

    private void fromIrStore(Instr irStore) {
        Unit uMain, uRes;
        if (irStore.main instanceof Var varMain) {
            if (!varMain.isArray) {
                uMain = new Unit(varMain);
            } else {
                uMain = null;
                assert false;
            }
        } else { // Const, Reg
            uMain = new Unit(irStore.main);
        }

        assert irStore.res instanceof Var;
        Var varRes = (Var) irStore.res;
        if (!varRes.isArray) {
            uRes = new Unit(varRes);
        } else {
            uRes = irStore.supl == null ?
                    new Unit(varRes, new Const(0)) :
                    new Unit(varRes, irStore.supl);
        }
        copy(uMain, uRes);
    }

    private void fromIrGetAddress(Instr irGetAddr) {
        assert irGetAddr.main instanceof Var;
        Var var = (Var) irGetAddr.main;
        Unit uRes = new Unit(irGetAddr.res);
        regsPool.currentOperands = new ArrayList<>(List.of(uRes));
        MipsReg rRes = getReg(uRes, false);

        if (var.isGlobal) {
            program.append(MipsInstr.genLa(rRes, var));
        } else {
            program.append(MipsInstr.genCalc(addi, rRes, r(fp), stack.varsOffset.get((Var) irGetAddr.main)));
        }
        invalidateMem(uRes);
    }

    private void fromIrDerefAndIndex(Instr irDeref) {
        Unit uDeref = new Unit(irDeref.main, irDeref.supl), uRes = new Unit(irDeref.res);
        copy(uDeref, uRes);
    }

    private void fromIrStrefAndIndex(Instr irStref) {
        Unit uDeref = new Unit(irStref.res, irStref.supl), uData = new Unit(irStref.main);
        copy(uData, uDeref);
    }

    private void fromIrMove(Instr irMove) {
        Unit f = new Unit(irMove.main), t = new Unit(irMove.res);
        copy(f, t);
    }

    private boolean isSigned16Const(Operand o) {
        if (!(o instanceof Const c)) return false;
        int val = c.num != null ? c.num : c.ch;
        return val >= -32768 && val < 32768;
    }

    private void fromIrAdd(Instr irAdd) {
        Unit a = new Unit(irAdd.main), b = new Unit(irAdd.supl), x = new Unit(irAdd.res);
        regsPool.currentOperands = new ArrayList<>(Arrays.asList(a, b, x));

        MipsInstr instr = null;
        if (isSigned16Const(a.operand) && (getReg(a, true, false) == null)) {
            MipsReg rB = getReg(b, true);
            MipsReg rX = getReg(x, false);
            instr = MipsInstr.genCalc(addi, rX, rB, (Const) a.operand);
        } else if (isSigned16Const(b.operand) && (getReg(b, true, false) == null)) {
            MipsReg rA = getReg(a, true);
            MipsReg rX = getReg(x, false);
            instr = MipsInstr.genCalc(addi, rX, rA, (Const) b.operand);
        }

        if (instr == null) {
            MipsReg rA = getReg(a, true);
            MipsReg rB = getReg(b, true);
            MipsReg rX = getReg(x, false);
            instr = MipsInstr.genCalc(addu, rX, rA, rB);
        }

        program.append(instr);
        invalidateMem(x);
    }

    private void fromIrSub(Instr irSub) {
        Unit a = new Unit(irSub.main), b = new Unit(irSub.supl), x = new Unit(irSub.res);
        regsPool.currentOperands = new ArrayList<>(Arrays.asList(a, b, x));
        MipsInstr instr = null;

        if (b.operand instanceof Const con) {
            int val = con.num != null ? con.num : con.ch;
            Const neg = new Const(-val);
            if (isSigned16Const(neg)) {
                MipsReg rA = getReg(a, true);
                MipsReg rX = getReg(x, false);
                instr = MipsInstr.genCalc(addi, rX, rA, neg);
            }
        }

        if (instr == null) {
            MipsReg rA = getReg(a, true);
            MipsReg rB = getReg(b, true);
            MipsReg rX = getReg(x, false);
            instr = MipsInstr.genCalc(subu, rX, rA, rB);
        }

        program.append(instr);
        invalidateMem(x);
    }

    private Integer powOf2(Operand o) {
        if (!(o instanceof Const c)) return null;
        int val = c.num != null ? c.num : c.ch;
        int ind = 0;
        while (val != 0 && val % 2 == 0) {
            val /= 2;
            ind++;
        }
        if (val != 1) {
            return null;
        } else {
            return ind < 32 ? ind : null;
        }
    }

    private void fromIrMul(Instr irMul) {
        Unit a = new Unit(irMul.main), b = new Unit(irMul.supl), x = new Unit(irMul.res);
        regsPool.currentOperands = new ArrayList<>(Arrays.asList(a, b, x));
        Integer i;
        if ((i = powOf2(a.operand)) != null) {
            MipsReg rB = getReg(b, true);
            MipsReg rX = getReg(x, false);
            program.append(MipsInstr.genCalc(sll, rX, rB, new Const(i)));
        } else if ((i = powOf2(b.operand)) != null) {
            MipsReg rA = getReg(a, true);
            MipsReg rX = getReg(x, false);
            program.append(MipsInstr.genCalc(sll, rX, rA, new Const(i)));
        } else {
            MipsReg rA = getReg(a, true);
            MipsReg rB = getReg(b, true);
            MipsReg rX = getReg(x, false);
            program.append(MipsInstr.genCalc(mulu, rX, rA, rB));
        }
        invalidateMem(x);
    }

    private void fromIrDiv(Instr irDiv) {
        Unit a = new Unit(irDiv.main), b = new Unit(irDiv.supl), x = new Unit(irDiv.res);
        regsPool.currentOperands = new ArrayList<>(Arrays.asList(a, b, x));
        MipsReg rA = getReg(a, true);
        MipsReg rB = getReg(b, true);
        MipsReg rX = getReg(x, false);
        program.append(MipsInstr.genCalc(div, null, rA, rB));
        program.append(MipsInstr.genMoveFrom(mflo, rX));
        invalidateMem(x);
    }

    private void fromIrMod(Instr irMod) {
        Unit a = new Unit(irMod.main), b = new Unit(irMod.supl), x = new Unit(irMod.res);
        regsPool.currentOperands = new ArrayList<>(Arrays.asList(a, b, x));
        MipsReg rA = getReg(a, true);
        MipsReg rB = getReg(b, true);
        MipsReg rX = getReg(x, false);
        program.append(MipsInstr.genCalc(div, null, rA, rB));
        program.append(MipsInstr.genMoveFrom(mfhi, rX));
        invalidateMem(x);
    }

    private void fromIrLss(Instr irLss) {
        Unit a = new Unit(irLss.main), b = new Unit(irLss.supl), x = new Unit(irLss.res);
        regsPool.currentOperands = new ArrayList<>(Arrays.asList(a, b, x));
        MipsReg rA = getReg(a, true);
        MipsReg rB = getReg(b, true);
        MipsReg rX = getReg(x, false);
        program.append(MipsInstr.genCalc(slt, rX, rA, rB));
        invalidateMem(x);
    }

    private void fromIrLeq(Instr irLeq) {
        Unit a = new Unit(irLeq.main), b = new Unit(irLeq.supl), x = new Unit(irLeq.res);
        regsPool.currentOperands = new ArrayList<>(Arrays.asList(a, b, x));
        MipsReg rA = getReg(a, true);
        MipsReg rB = getReg(b, true);
        MipsReg rX = getReg(x, false);
        program.append(MipsInstr.genCalc(sle, rX, rA, rB));
        invalidateMem(x);
    }

    private void fromIrGre(Instr irGre) {
        Unit a = new Unit(irGre.main), b = new Unit(irGre.supl), x = new Unit(irGre.res);
        regsPool.currentOperands = new ArrayList<>(Arrays.asList(a, b, x));
        MipsReg rA = getReg(a, true);
        MipsReg rB = getReg(b, true);
        MipsReg rX = getReg(x, false);
        program.append(MipsInstr.genCalc(sgt, rX, rA, rB));
        invalidateMem(x);
    }

    private void fromIrGeq(Instr irGeq) {
        Unit a = new Unit(irGeq.main), b = new Unit(irGeq.supl), x = new Unit(irGeq.res);
        regsPool.currentOperands = new ArrayList<>(Arrays.asList(a, b, x));
        MipsReg rA = getReg(a, true);
        MipsReg rB = getReg(b, true);
        MipsReg rX = getReg(x, false);
        program.append(MipsInstr.genCalc(sge, rX, rA, rB));
        invalidateMem(x);
    }

    private void fromIrEql(Instr irEql) {
        Unit a = new Unit(irEql.main), b = new Unit(irEql.supl), x = new Unit(irEql.res);
        regsPool.currentOperands = new ArrayList<>(Arrays.asList(a, b, x));
        MipsReg rA = getReg(a, true);
        MipsReg rB = getReg(b, true);
        MipsReg rX = getReg(x, false);
        program.append(MipsInstr.genCalc(seq, rX, rA, rB));
        invalidateMem(x);
    }

    private void fromIrNeq(Instr irNeq) {
        Unit a = new Unit(irNeq.main), b = new Unit(irNeq.supl), x = new Unit(irNeq.res);
        regsPool.currentOperands = new ArrayList<>(Arrays.asList(a, b, x));
        MipsReg rA = getReg(a, true);
        MipsReg rB = getReg(b, true);
        MipsReg rX = getReg(x, false);
        program.append(MipsInstr.genCalc(sne, rX, rA, rB));
        invalidateMem(x);
    }

    private void fromIrLabel(Instr irLabel) {
        regsPool.flushToMem();
        regsPool.reset();
        program.append(MipsInstr.genLabel((Label) irLabel.res));
    }

    private void fromIrGoto(Instr irGoto) {
        regsPool.flushToMem();
        program.append(MipsInstr.genJump((Label) irGoto.res));
        regsPool.reset();
    }

    private void fromIrGoif(Instr irGoif) {
        Unit uCond = new Unit(irGoif.main);
        regsPool.currentOperands = new ArrayList<>(List.of(uCond));
        MipsReg r = getReg(uCond, true);

        regsPool.flushToMem();
        program.append(MipsInstr.genBne(r, r(zero), (Label) irGoif.res));
        regsPool.reset();
    }

    private void fromIrGont(Instr irGont) {
        Unit u = new Unit(irGont.main);
        regsPool.currentOperands = new ArrayList<>(List.of(u));
        MipsReg r = getReg(u, true);

        regsPool.flushToMem();
        program.append(MipsInstr.genBeq(r, r(zero), (Label) irGont.res));
        regsPool.reset();
    }

    private boolean firstParam = true;
    private boolean enteredTextSeg = false;
    private boolean isMain = false;
    private FuncRef curFunc = null;
    private void fromIrFunc(Instr irFunc) {
        stack.reset();
        regsPool.reset();
        firstParam = true;

        // Prepare param entries.
        for (Var varParam : ((FuncRef) irFunc.res).params) {
            stack.allocMem(varParam);
        }
        stack.allocMem(4); // simulate memory for $ra
        int translateAmount = -stack.top;
        for (Map.Entry<Var, Const> entry : stack.varsOffset.entrySet()) {
            entry.setValue(new Const(entry.getValue().num + translateAmount));
        }
        // Now we have param entries, just reset nextMem again.
        stack.top = 0;

        if (!enteredTextSeg) {
            enteredTextSeg = true;
            program.append(MipsInstr.genTextSeg());
        }
        curFunc = (FuncRef) irFunc.res;
        program.append(MipsInstr.genLabel(curFunc));
        isMain = curFunc.funcName.equals("main");

        // Generate function head.
        program.append(MipsInstr.genMem(sw, r(fp), r(sp), new Const(-4)));
        program.append(MipsInstr.genMove(r(fp), r(sp)));
        stack.allocMem(4);
        if (!isMain)
            regsPool.saveRegs();

        // Init global allocated params.
        for (Var param : curFunc.params) {
            Unit uParam = new Unit(param);
            MipsReg globReg = globalAlloc.query(uParam);
            if (globReg != null) {
                stack.loadUnit(uParam, globReg);
            }
        }
    }

    private int lastSyscall = -1;
    private void fromIrParam(Instr irParam) {
        Instr next = irInstrs.get(irInstrs.indexOf(irParam) + 1);
        if (next.op == CALL
                && List.of("putint", "putchar").contains(((FuncRef) next.main).funcName)) {
            Unit u = new Unit(irParam.main);
            regsPool.currentOperands = new ArrayList<>(List.of(u));
            MipsReg r = getReg(u, true, false);
            if (r == null) {
                stack.loadUnit(u, r(a0));
            } else {
                program.append(MipsInstr.genMove(r(a0), r));
            }
            switch (((FuncRef) next.main).funcName) {
                case "putchar" -> {
                    if (lastSyscall != 11)
                        program.append(MipsInstr.genLi(r(v0), new Const(11)));
                    lastSyscall = 11;
                }
                case "putint" -> {
                    if (lastSyscall != 1)
                        program.append(MipsInstr.genLi(r(v0), new Const(1)));
                    lastSyscall = 1;
                }
                default -> { assert false; }
            }
        } else {
            if (firstParam) {
                regsPool.flushToMem(); // checkme
                firstParam = false;
                stack.recordTop();
                stack.alignMem(4); // To ensure same memory layout in caller & callee.
            }

            // Push param.
            Unit u = new Unit(irParam.main);
            regsPool.currentOperands = new ArrayList<>(List.of(u));
            MipsReg r = getReg(u, true, false);
            if (r == null) {
                r = r(a0);
                stack.loadUnit(u, r(a0));
            }
            // Use "new Reg()" to force allocate new stack memory for parameter.
            // Reference is thrown away for there's no future use of real parameter.
            // For now, push all params on stack.
            stack.storeUnit(new Unit(new Reg(irParam.type)), r); // No one will remember you since, ever.
        }
    }

    private void fromIrCall(Instr irCall) {
        if (List.of("putint", "putchar").contains(((FuncRef) irCall.main).funcName)) {
            program.append(MipsInstr.genSyscall());
        } else {

            if (firstParam) { // If no param, falls here.
                regsPool.flushToMem(); // checkme
                firstParam = false;
                stack.recordTop();
                stack.alignMem(4); // To ensure same memory layout in caller & callee.
            }

            stack.allocMem(4);
            program.append(MipsInstr.genCalc(addi, r(sp), r(fp), new Const(stack.top)));
            program.append(MipsInstr.genMem(sw, r(ra), r(sp), new Const(0)));
            program.append(MipsInstr.genJal((FuncRef) irCall.main));
            program.append(MipsInstr.genMem(lw, r(ra), r(sp), new Const(0)));

            // Recover param status.
            program.append(MipsInstr.genCalc(addi, r(sp), r(fp), new Const(stack.record)));
            firstParam = true;
            stack.restoreTop();
            regsPool.reset();

            if (irCall.res != null) {
                Unit u = new Unit(irCall.res);
                regsPool.currentOperands = new ArrayList<>(List.of(u));
                if (!u.uncertain()) {
                    MipsReg rT = getReg(u, false);
                    if (u.operand.type == i8) {
                        program.append(MipsInstr.genCalc(andi, rT, r(v0), new Const(0xFF)));
                    } else {
                        program.append(MipsInstr.genMove(rT, r(v0)));
                    }
                } else {
                    stack.storeUnit(u, r(v0));
                }
                invalidateMem(u);
            }
        }
    }

    private void fromIrRet(Instr irRet) {
        if (irRet.main != null) {
            // Prepare return value in $v0.
            Unit u = new Unit(irRet.main);
            regsPool.currentOperands = new ArrayList<>(List.of(u));
            MipsReg r = getReg(u, true, false); // checkme Should we do this?
            if (r != null) {
                program.append(MipsInstr.genMove(r(v0), r));
            } else {
                stack.loadUnit(u, r(v0));
            }
        }

        // Generate function tail.
        regsPool.flushToMem();
        if (!isMain)
            regsPool.restoreRegs();
        program.append(MipsInstr.genMove(r(sp), r(fp)));
        program.append(MipsInstr.genMem(lw, r(fp), r(sp), new Const(-4)));
        program.append(MipsInstr.genJumpReg(r(ra)));
        regsPool.reset();
    }

    /*                Helpers functions & Sub Classes Below              */
    private boolean isTempReg(MipsReg reg) {
        return regsPool.regs.contains(reg);
    }

    private void copy(Unit from, Unit to) {
        regsPool.currentOperands = new ArrayList<>(Arrays.asList(from, to));
        if (!from.uncertain()) {
            MipsReg rF = getReg(from, true);
            if (isTempReg(rF)) { // Temp alloc, in some reg
                if (!to.uncertain()) {
                    if (from.operand.type == i32 && to.operand.type == i8) {
                        MipsReg rT = getReg(to, false);
                        program.append(MipsInstr.genCalc(andi, rT, rF, new Const(0xFF)));
                    } else {
                        if (globalAlloc.query(to) != null) {
                            MipsReg rT = getReg(to, false);
                            program.append(MipsInstr.genMove(rT, rF));
                        } else {
                            regsPool.attachToReg(to, rF);
                        }
                    }
                } else {
                    stack.storeUnit(to, rF);
                }
            } else { // Glob alloc, in some reg
                if (!to.uncertain()) {
                    MipsReg rT = getReg(to, false);
                    if (from.operand.type == i32 && to.operand.type == i8) {
                        program.append(MipsInstr.genCalc(andi, rT, rF, new Const(0xFF)));
                    } else {
                        program.append(MipsInstr.genMove(rT, rF));
                    }
                } else {
                    stack.storeUnit(to, rF);
                }
            }
        } else {
            if (!to.uncertain()) {
                MipsReg rT = getReg(to, false);
                stack.loadUnit(from, rT);
            } else {
                stack.loadUnit(from, r(v1));
                stack.storeUnit(to, r(v1));
            }
        }
        invalidateMem(to);
    }

    private MipsReg getReg(Unit unit, boolean isRead) {
        return getReg(unit, isRead, true);
    }

    private MipsReg getReg(Unit unit, boolean isRead, boolean alloc) {
        assert !unit.uncertain();

        // Search for any allocated global register.
        MipsReg globalReg = globalAlloc.query(unit);
        if (globalReg != null) return globalReg;

        // Use register pool.
        return regsPool.getReg(unit, isRead, alloc);
    }

    /**
     * Mark stack memory location as invalid if the unit is stored with temp allocation.
     * Do nothing if the unit is assigned with global allocation or uncertain(no available allocation).
     */
    private void invalidateMem(Unit unit) {
        regsPool.memValidation.replace(unit, false);
    }

    // 1. Memory Simulation Components: Regs & Stack

    private final boolean regInfo = false;
    private final class RegsPool {
        // Note: "var" in this scope refers to IR operands, and "reg" are real MIPS registers.
        private final ArrayList<MipsReg> regs = new ArrayList<>(Arrays.asList(
                r(t0), r(t1), r(t2), r(t3), r(t4), r(t5), r(t6), r(t7), r(t8), r(t9)));
        private final HashMap<MipsReg, HashSet<Unit>> regsMap = new HashMap<>(); {
            for (MipsReg reg : regs) regsMap.put(reg, new HashSet<>());
        }
        private final HashMap<Unit, HashSet<MipsReg>> varsMap = new HashMap<>();
        private final HashMap<Unit, Boolean> memValidation = new HashMap<>();

        private void reset() {
            regsMap.clear();
            for (MipsReg reg : regs) regsMap.put(reg, new HashSet<>());
            varsMap.clear();
            memValidation.clear();
        }
        private ArrayList<Unit> savedUnit;
        private ArrayList<MipsReg> savedRegs;
        private void saveRegs() {
            int nReg = curFunc.allocated.size();

            savedUnit = new ArrayList<>();
            for (int i = 0; i < nReg; i++) {
                savedUnit.add(new Unit(new Reg(i32)));
            }

            savedRegs = new ArrayList<>(curFunc.allocated);
            savedRegs.sort(Comparator.comparing(o -> o.id));
            for (MipsReg reg : savedRegs) {
                int i = savedRegs.indexOf(reg);
                stack.storeUnit(savedUnit.get(i), reg);
            }
        }
        private void restoreRegs() {
            for (int i = 0; i < savedUnit.size(); i++) {
                stack.loadUnit(savedUnit.get(i), savedRegs.get(i));
            }
        }
        private void flushToMem() {
            lastSyscall = -1;
            for (MipsReg reg : regs) {
                for (Unit unit : regsMap.get(reg)) {
                    if (!memValidation.get(unit)) {
                        stack.storeUnit(unit, reg);
                        memValidation.replace(unit, true);
                    }
                }
            }
        }
        private void registerUnit(Unit unit) {
            // Auto register a unit on first use.
            if (!varsMap.containsKey(unit)) {
                varsMap.put(unit, new HashSet<>());
                memValidation.put(unit, true);
            }
        }
        private MipsReg getReg(Unit unit, boolean isRead, boolean alloc) {
            registerUnit(unit);
            // Search for any assigned register for the unit.
            if (!varsMap.get(unit).isEmpty()) {
                if (isRead) {
                    return (MipsReg) varsMap.get(unit).toArray()[0];
                } else {
                    MipsReg reg = null;
                    // Try to find an OCCUPIED register.
                    for (MipsReg r : varsMap.get(unit)) {
                        if (regsMap.get(r).size() == 1 && regsMap.get(r).contains(unit)) {
                            reg = r;
                            break;
                        }
                    }
                    // Remove old bindings.
                    for (MipsReg r : varsMap.get(unit)) {
                        if (r != reg) {
                            regsMap.get(r).remove(unit);
                            if (regInfo) program.append(MipsInstr.genComment(r + " => " + unit));
                        }
                    }
                    varsMap.get(unit).clear();
                    if (reg != null) varsMap.get(unit).add(reg);
                    // If no occupied register found, alloc a new one.
                    if (reg == null) {
                        if (!alloc) return null; // checkme
                        reg = newReg();
                        regsMap.get(reg).add(unit);
                        varsMap.get(unit).add(reg);
                        if (regInfo) program.append(MipsInstr.genComment(reg + " <= " + unit));
                    }
                    return reg;
                }
            } else { // No register for unit, assign a new one.
                if (!alloc) return null;
                MipsReg reg = newReg();
                regsMap.get(reg).add(unit);
                varsMap.get(unit).add(reg);
                if (regInfo) program.append(MipsInstr.genComment(reg + " <= " + unit));
                if (isRead) {
                    assert memValidation.get(unit);
                    // unit mem -> reg
                    stack.loadUnit(unit, reg);
                }
                return reg;
            }
        }
        private void attachToReg(Unit unit, MipsReg reg) {
            registerUnit(unit);
            // Forget old info.
            for (MipsReg oldReg : varsMap.get(unit)) {
                regsMap.get(oldReg).remove(unit);
                if (regInfo) program.append(MipsInstr.genComment(oldReg + " => " + unit));
            }
            varsMap.get(unit).clear();

            // Register new info.
            if (regInfo) program.append(MipsInstr.genComment(reg + " <= " + unit));
            regsMap.get(reg).add(unit);
            varsMap.get(unit).add(reg);
        }
        private int toKick = 0;
        private final int numRegs = regs.size();
        private ArrayList<Unit> currentOperands;
        private MipsReg newReg() {
            // Try to find a free reg.
            for (MipsReg reg : regs) {
                if (regsMap.get(reg).isEmpty()) {
                    return reg;
                }
            }
            // If no free reg, find one to kick:
            assert currentOperands != null;
            while (true) {
                boolean kickingOperand = false;
                for (Unit unit : currentOperands) {
                    kickingOperand |= varsMap.containsKey(unit)
                            && varsMap.get(unit).contains(regs.get(toKick));
                }
                if (!kickingOperand) { break; }
                toKick = (toKick + 1) % numRegs;
            }
            MipsReg reg = regs.get(toKick); // This reg is spilled.
            toKick = (toKick + 1) % numRegs;

            // Prepare the reg.
            for (Unit unit : regsMap.get(reg)) {
                if (regInfo) program.append(MipsInstr.genComment(reg + " => " + unit));
                varsMap.get(unit).remove(reg);
                // Only write back when memory invalid.
                if (!memValidation.get(unit)) {
                    memValidation.replace(unit, true);
                    // reg -> unit mem
                    stack.storeUnit(unit, reg);
                }
            }
            regsMap.get(reg).clear();
            return reg;
        }
    }

    private final class Stack {
        private int top = 0;
        private int record = 0; // Used before a procedural call.
        private final HashMap<Reg, Const> regsOffset = new HashMap<>();
        private final HashMap<Var, Const> varsOffset = new HashMap<>();

        private void reset() {
            top = 0;
            record = 0;
            regsOffset.clear();
            varsOffset.clear();
        }
        private void recordTop() { record = top; }
        private void restoreTop() { top = record; }
        private void alignMem(int align) {
            int prominent = (-top) % align;
            int alter = prominent == 0 ? 0 : align - prominent;
            top -= alter;
        }
        private void allocMem(int size) {
            alignMem(size);
            top -= size;
        }
        private Const allocMem(Reg reg) {
            int size = reg.type.size();

            alignMem(size);

            top -= size;
            Const addr = new Const(top); // Base addr is after stack push!
            regsOffset.put(reg, addr);

            return addr;
        }
        private Const allocMem(Var var) {
            int size = var.isReference ? i32.size() : var.type.size();

            alignMem(size);
            if (var.isArray) {
                assert !var.isReference;
                size *= var.arrayLength;
            }
            top -= size;

            Const addr = new Const(top);
            varsOffset.put(var, addr);

            return addr;
        }
        private Const offsetOf(Reg reg) {
            return regsOffset.get(reg);
        }
        private Const offsetOf(Var var) {
            return varsOffset.get(var);
        }
        private void loadUnit(Unit unit, MipsReg reg) {
            Operand operand = unit.operand;
            Operand index = unit.arrayIndex;
            switch (unit.type) {
                case VAL -> {
                    Var var = (Var) operand;
                    if (!var.isGlobal) {
                        program.append(MipsInstr.genMem(var.type == i8 ? lb : lw, reg, r(fp), offsetOf(var)));
                    } else {
                        program.append(MipsInstr.genLa(r(gp), var));
                        program.append(MipsInstr.genMem(var.type == i8 ? lb : lw, reg, r(gp), new Const(0)));
                    }
                }
                case REF -> {
                    Var var = (Var) operand;
                    if (index == null) {
                        program.append(MipsInstr.genMem(lw, reg, r(fp), offsetOf(var)));
                    } else {
                        Unit uBase = new Unit(var);
                        regsPool.currentOperands.add(uBase);
                        if (index instanceof Const) {
                            MipsReg rBase = getReg(uBase, true, false);
                            if (rBase == null) {
                                rBase = r(gp);
                                stack.loadUnit(uBase, rBase);
                            }
                            int ofs = ((Const) index).num * var.type.size();
                            program.append(MipsInstr.genMem(var.type == i8 ? lb : lw, reg, rBase, new Const(ofs)));
                        } else {
                            Unit uInd = new Unit(index);
                            regsPool.currentOperands.add(uInd);
                            MipsReg rInd = getReg(uInd, true, false);
                            if (rInd == null) {
                                rInd = r(a0);
                                stack.loadUnit(uInd, rInd);
                            }
                            MipsReg rBase = getReg(uBase, true, false);
                            if (rBase == null) {
                                rBase = r(gp);
                                stack.loadUnit(uBase, rBase);
                            }
                            program.append(MipsInstr.genMove(r(a0), rInd));
                            if (var.type == i32) {
                                program.append(MipsInstr.genCalc(sll, r(a0), r(a0), new Const(2)));
                            }
                            program.append(MipsInstr.genCalc(addu, r(a0), r(a0), rBase));
                            program.append(MipsInstr.genMem(var.type == i8 ? lb : lw, reg, r(a0), new Const(0)));
                        }
                    }
                }
                case ARR -> {
                    Var var = (Var) operand;
                    if (index instanceof Const c) {
                        int ofs = c.num * var.type.size();
                        if (!var.isGlobal) {
                            program.append(MipsInstr.genMem(var.type == i8 ? lb : lw, reg, r(fp), new Const(offsetOf(var).num + ofs)));
                        } else {
                            program.append(MipsInstr.genLa(r(gp), var));
                            program.append(MipsInstr.genMem(var.type == i8 ? lb : lw, reg, r(gp), new Const(ofs)));
                        }
                    } else {
                        Unit uInd = new Unit(index);
                        MipsReg rInd = getReg(uInd, true, false);
                        if (rInd == null) {
                            rInd = r(v1);
                            stack.loadUnit(uInd, rInd);
                        }
                        program.append(MipsInstr.genMove(r(v1), rInd));
                        if (!var.isGlobal) {
                            if (var.type == i32) {
                                program.append(MipsInstr.genCalc(sll, r(v1), r(v1), new Const(2)));
                            }
                            program.append(MipsInstr.genCalc(addu, r(v1), r(fp), r(v1)));
                            program.append(MipsInstr.genMem(var.type == i8 ? lb : lw, reg, r(v1), new Const(offsetOf(var).num)));
                        } else {
                            program.append(MipsInstr.genLa(r(gp), var));
                            if (var.type == i32) {
                                program.append(MipsInstr.genCalc(sll, r(v1), r(v1), new Const(2)));
                            }
                            program.append(MipsInstr.genCalc(addu, r(gp), r(gp), r(v1)));
                            program.append(MipsInstr.genMem(var.type == i8 ? lb : lw, reg, r(gp), new Const(0)));
                        }
                    }
                }
                case REG -> {
                    Reg temp = (Reg)  operand;
                    program.append(MipsInstr.genMem(temp.type == i8 ? lb : lw, reg, r(fp), offsetOf(temp)));
                }
                case CON -> {
                    Const con = (Const) operand;
                    program.append(MipsInstr.genLi(reg, con));
                }
            }
        }
        private void storeUnit(Unit unit, MipsReg reg) {
            Operand to = unit.operand;
            Operand index = unit.arrayIndex;
            switch (unit.type) {
                case VAL -> {
                    Var var = (Var) to;
                    Instr.Type type = var.type;
                    if (var.isGlobal) {
                        program.append(MipsInstr.genLa(r(gp), var));
                        program.append(MipsInstr.genMem(type == i8 ? sb : sw, reg, r(gp), new Const(0)));
                    } else {
                        program.append(MipsInstr.genMem(type == i8 ? sb : sw, reg, r(fp), offsetOf(var)));
                    }
                }
                case REF -> {
                    Var var = (Var) to;
                    if (index == null) {
                        program.append(MipsInstr.genMem(sw, reg, r(fp), offsetOf(var)));
                    } else {
                        Unit uBase = new Unit(var);
                        regsPool.currentOperands.add(uBase);
                        if (index instanceof Const) {
                            MipsReg rBase = getReg(uBase, true, false);
                            if (rBase == null) {
                                rBase = r(gp);
                                stack.loadUnit(uBase, rBase);
                            }
                            int ofs = ((Const) index).num * var.type.size();
                            program.append(MipsInstr.genMem(var.type == i8 ? sb : sw, reg, rBase, new Const(ofs)));
                        } else {
                            Unit uInd = new Unit(index);
                            regsPool.currentOperands.add(uInd);
                            MipsReg rInd = getReg(uInd, true, false);
                            if (rInd == null) {
                                rInd = r(a0);
                                stack.loadUnit(uInd, rInd);
                            } else {
                                program.append(MipsInstr.genMove(r(a0), rInd));
                            }
                            MipsReg rBase = getReg(uBase, true, false);
                            if (rBase == null) {
                                rBase = r(gp);
                                stack.loadUnit(uBase, rBase);
                            }
                            if (var.type == i32) {
                                program.append(MipsInstr.genCalc(sll, r(a0), r(a0), new Const(2)));
                            }
                            program.append(MipsInstr.genCalc(addu, r(a0), r(a0), rBase));
                            program.append(MipsInstr.genMem(var.type == i8 ? sb : sw, reg, r(a0), new Const(0)));
                        }
                    }
                }
                case ARR -> {
                    Var var = (Var) to;
                    if (index instanceof Const c) {
                        int ofs = c.num * var.type.size();
                        if (var.isGlobal) {
                            program.append(MipsInstr.genLa(r(gp), var));
                            program.append(MipsInstr.genMem(var.type == i8 ? sb : sw, reg, r(gp), new Const(ofs)));
                        } else {
                            int base = offsetOf(var).num;
                            program.append(MipsInstr.genMem(to.type == i8 ? sb : sw, reg, r(fp), new Const(base + ofs)));
                        }
                    } else {
                        Unit uInd = new Unit(index);
                        MipsReg rInd = getReg(uInd, true, false);
                        if (rInd == null) {
                            rInd = r(v1);
                            stack.loadUnit(uInd, rInd);
                        }
                        program.append(MipsInstr.genMove(r(v1), rInd));
                        if (!var.isGlobal) {
                            int base = offsetOf(var).num;
                            if (var.type == i32)
                                program.append(MipsInstr.genCalc(sll, r(v1), r(v1), new Const(2)));
                            program.append(MipsInstr.genCalc(addu, r(v1), r(fp), r(v1)));
                            program.append(MipsInstr.genMem(var.type == i8 ? sb : sw, reg, r(v1), new Const(base)));
                        } else {
                            program.append(MipsInstr.genLa(r(gp), var));
                            if (var.type == i32)
                                program.append(MipsInstr.genCalc(sll, r(v1), r(v1), new Const(2)));
                            program.append(MipsInstr.genCalc(addu, r(gp), r(gp), r(v1)));
                            program.append(MipsInstr.genMem(var.type == i8 ? sb : sw, reg, r(gp), new Const(0)));
                        }
                    }
                }
                case REG -> {
                    Reg temp = (Reg) to;
                    if (regsOffset.containsKey(temp)) {
                        program.append(MipsInstr.genMem(to.type == i8 ? sb : sw, reg, r(fp), offsetOf(temp)));
                    } else {
                        program.append(MipsInstr.genMem(to.type == i8 ? sb : sw, reg, r(fp), allocMem(temp)));
                    }
                }
                case CON -> { }
            }
        }
    }
}
