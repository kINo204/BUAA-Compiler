package mips;

import mips.datastruct.MipsInstr;
import mips.datastruct.MipsProgram;

import java.util.ArrayList;

public interface MipsTranslator {
    MipsProgram translate();
}
