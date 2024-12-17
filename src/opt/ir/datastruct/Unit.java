package opt.ir.datastruct;

import ir.datastruct.operand.Const;
import ir.datastruct.operand.Operand;
import ir.datastruct.operand.Reg;
import ir.datastruct.operand.Var;

/* To produce memory motions within translations, we need to simulate
both the registers and the linear main memory model. To represent a
"unit" of this management, we may define a "Unit" here:
 */
public class Unit {
    public enum Type {VAL, REF, ARR, REG, CON}

    public final Type type;

    /* One of the main reasons for introducing "Unit" is, by tracking
    the index of either array element or de-reference, we may treat
    each element of a list individually. For example, when considering
    allocating global registers, an array element may be included in
    the awaiting variables, increasing the flexibilities of multiple
    strategies.
    */
    public final Operand operand;
    public final Operand arrayIndex;

    public Unit(Operand operand) {
        this.operand = operand;
        if (operand instanceof Var v) {
            if (!v.isArray) {
                if (!v.isReference) {
                    type = Type.VAL;
                } else {
                    type = Type.REF;
                }
            } else {
                type = Type.ARR;
            }
        } else if (operand instanceof Reg) {
            type = Type.REG;
        } else {
            type = Type.CON;
        }
        this.arrayIndex = null;
    }

    public Unit(Operand operand, Operand arrayIndex) {
        this.operand = operand;
        if (operand instanceof Var v) {
            if (!v.isArray) {
                if (!v.isReference) {
                    type = Type.VAL;
                } else {
                    type = Type.REF;
                }
            } else {
                type = Type.ARR;
            }
        } else if (operand instanceof Reg) {
            type = Type.REG;
        } else {
            type = Type.CON;
        }
        this.arrayIndex = arrayIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Unit unit)) return false;

        if (this.uncertain() || unit.uncertain()) return false;
        if (arrayIndex == null && unit.arrayIndex != null) return false;
        if (arrayIndex != null && unit.arrayIndex == null) return false;
        if (arrayIndex != null && !arrayIndex.equals(unit.arrayIndex)) return false;
        return operand.equals(unit.operand);
    }

    @Override
    public int hashCode() {
        int result = operand != null ? operand.hashCode() : 0;
        result = 31 * result + (arrayIndex != null ? arrayIndex.hashCode() : 0);
        return result;
    }

    /* Of course, we've not sure yet if this strategy would do good
    to the performance or not, so there's a "switch" here to define
    excluded types of units. Add the desired type here, and it will be
    excluded from many strategies.
     */
    public boolean uncertain() {
        return
                arrayIndex != null && !(arrayIndex instanceof Const);
    }

    @Override
    public String toString() {
        assert operand != null;
        return arrayIndex == null ? operand.toString() :
                operand.toString() + "[" + arrayIndex + "]";
    }
}
