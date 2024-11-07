package mips;

public final class MipsUtils {
    /* OS utils. */
    public static final String osWrapper = """
            # [compiler-generated](os-wrapper)
            jal     main
            move    $a0, $v0
            li      $v0, 17
            syscall
            
    """;

}
