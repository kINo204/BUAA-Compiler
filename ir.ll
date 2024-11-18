define fun func:
	if false 1 goto  $L0_if_end

$L0_if_end:
	ret

define fun main:
	if false 1 goto  $L1_if_end

$L1_if_end:
	ret: i32  0