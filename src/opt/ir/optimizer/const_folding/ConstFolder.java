package opt.ir.optimizer.const_folding;

import ir.datastruct.Instr;
import ir.datastruct.operand.Const;
import opt.ir.datastruct.BBlock;
import opt.ir.datastruct.Cfg;

public class ConstFolder {
    private final Cfg cfg;

    public ConstFolder(Cfg cfg) {
        this.cfg = cfg;
    }

    public boolean run() {
        boolean mod = false;

        for (BBlock block : cfg.blocks) {
            for (Instr instr : block.instrs) {
                switch (instr.op) {
                    case ADD -> {
                        if (instr.main instanceof Const c1 && instr.supl instanceof Const c2) {
                            mod = true;
                            instr.op = Instr.Operator.MOVE;
                            instr.supl = null;
                            int v1 = c1.num != null ? c1.num : c1.ch;
                            int v2 = c2.num != null ? c2.num : c2.ch;
                            instr.main = new Const(v1 + v2);
                        } else if (instr.main instanceof Const c1) {
                            int v1 = c1.num != null ? c1.num : c1.ch;
                            if (v1 == 0) {
                                mod = true;
                                instr.op = Instr.Operator.MOVE;
                                instr.main = instr.supl;
                                instr.supl = null;
                            }
                        } else if (instr.supl instanceof Const c2) {
                            int v2 = c2.num != null ? c2.num : c2.ch;
                            if (v2 == 0) {
                                mod = true;
                                instr.op = Instr.Operator.MOVE;
                                instr.supl = null;
                            }
                        }
                    }
                    case SUB -> {
                        if (instr.main instanceof Const c1 && instr.supl instanceof Const c2) {
                            mod = true;
                            instr.op = Instr.Operator.MOVE;
                            instr.supl = null;
                            int v1 = c1.num != null ? c1.num : c1.ch;
                            int v2 = c2.num != null ? c2.num : c2.ch;
                            instr.main = new Const(v1 - v2);
                        } else if (instr.supl instanceof Const c2) {
                            int v2 = c2.num != null ? c2.num : c2.ch;
                            if (v2 == 0) {
                                mod = true;
                                instr.op = Instr.Operator.MOVE;
                                instr.supl = null;
                            }
                        }
                    }
                    case MUL -> {
                        if (instr.main instanceof Const c1 && instr.supl instanceof Const c2) {
                            mod = true;
                            instr.op = Instr.Operator.MOVE;
                            instr.supl = null;
                            int v1 = c1.num != null ? c1.num : c1.ch;
                            int v2 = c2.num != null ? c2.num : c2.ch;
                            instr.main = new Const(v1 * v2);
                        } else if (instr.main instanceof Const c1) {
                            int v1 = c1.num != null ? c1.num : c1.ch;
                            if (v1 == 0) {
                                mod = true;
                                instr.op = Instr.Operator.MOVE;
                                instr.main = new Const(0);
                                instr.supl = null;
                            } else if (v1 == 1) {
                                mod = true;
                                instr.op = Instr.Operator.MOVE;
                                instr.main = instr.supl;
                                instr.supl = null;
                            }
                        } else if (instr.supl instanceof Const c2) {
                            int v2 = c2.num != null ? c2.num : c2.ch;
                            if (v2 == 0) {
                                mod = true;
                                instr.op = Instr.Operator.MOVE;
                                instr.main = new Const(0);
                                instr.supl = null;
                            } else if (v2 == 1) {
                                mod = true;
                                instr.op = Instr.Operator.MOVE;
                                instr.supl = null;
                            }
                        }
                    }
                    case DIV -> {
                        if (instr.main instanceof Const c1 && instr.supl instanceof Const c2) {
                            instr.op = Instr.Operator.MOVE;
                            int v1 = c1.num != null ? c1.num : c1.ch;
                            int v2 = c2.num != null ? c2.num : c2.ch;
                            if (v2 != 0) {
                                mod = true;
                                instr.main = new Const(v1 / v2);
                                instr.supl = null;
                            }
                        } else if (instr.main instanceof Const c1) {
                            int v1 = c1.num != null ? c1.num : c1.ch;
                            if (v1 == 0) {
                                mod = true;
                                instr.op = Instr.Operator.MOVE;
                                instr.main = new Const(0);
                                instr.supl = null;
                            }
                        } else if (instr.supl instanceof Const c2) {
                            int v2 = c2.num != null ? c2.num : c2.ch;
                            if (v2 == 1) {
                                mod = true;
                                instr.op = Instr.Operator.MOVE;
                                instr.supl = null;
                            }
                        }
                    }
                    case MOD -> {
                        if (instr.main instanceof Const c1 && instr.supl instanceof Const c2) {
                            instr.op = Instr.Operator.MOVE;
                            int v1 = c1.num != null ? c1.num : c1.ch;
                            int v2 = c2.num != null ? c2.num : c2.ch;
                            if (v2 != 0) {
                                mod = true;
                                instr.main = new Const(v1 % v2);
                                instr.supl = null;
                            }
                        } else if (instr.main instanceof Const c1) {
                            int v1 = c1.num != null ? c1.num : c1.ch;
                            if (v1 == 0) {
                                mod = true;
                                instr.op = Instr.Operator.MOVE;
                                instr.main = new Const(0);
                                instr.supl = null;
                            }
                        } else if (instr.supl instanceof Const c2) {
                            int v2 = c2.num != null ? c2.num : c2.ch;
                            if (v2 == 1) {
                                mod = true;
                                instr.op = Instr.Operator.MOVE;
                                instr.main = new Const(0);
                                instr.supl = null;
                            }
                        }
                    }
                    case LSS, LEQ, GRE, GEQ, EQL, NEQ -> {
                        if (instr.main instanceof Const c1 && instr.supl instanceof Const c2) {
                            mod = true;
                            int v1 = c1.num != null ? c1.num : c1.ch;
                            int v2 = c2.num != null ? c2.num : c2.ch;
                            instr.supl = null;
                            Const t = new Const(1), f = new Const(0);
                            instr.main = switch (instr.op) {
                                case LSS -> v1 <  v2 ? t : f;
                                case LEQ -> v1 <= v2 ? t : f;
                                case GRE -> v1 >  v2 ? t : f;
                                case GEQ -> v1 >= v2 ? t : f;
                                case EQL -> v1 == v2 ? t : f;
                                case NEQ -> v1 != v2 ? t : f;
                                default -> {
                                    assert false;
                                    yield null;
                                }
                            };
                            instr.op = Instr.Operator.MOVE;
                        }
                    }
                }
            }
        }

        return mod;
    }
}
