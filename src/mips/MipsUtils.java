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

    // Put at tail of program.
    public static final String lib_io = """
            # [compiler-generated](lib_io)
    .text
    putchar:
            sw	$fp, -4($sp)
            move	$fp, $sp
            addi	$sp, $sp, -4
                
            lb  $a0, 7($fp)
            li  $v0, 11
            syscall
            
            move	$sp, $fp
            lw	$fp, -4($sp)
            jr  $ra
            
    putint:
            sw	$fp, -4($sp)
            move	$fp, $sp
            addi	$sp, $sp, -4
                
            lw  $a0, 4($fp)
            li  $v0, 1
            syscall
            
            move	$sp, $fp
            lw	$fp, -4($sp)
            jr  $ra
            
    """;

}
