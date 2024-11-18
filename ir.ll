define fun main:
	i_2 = alloc: i32
	i_2: i32 = 0
	if false 1 goto  $L0_if_else
	a_3 = alloc: i32
	goto  $L1_if_end

$L0_if_else:
	if false 2 goto  $L2_if_else
	b_4 = alloc: i32
	goto  $L3_if_end

$L2_if_else:
	c_5 = alloc: i32

$L3_if_end:

$L1_if_end:
	ret: i32  0