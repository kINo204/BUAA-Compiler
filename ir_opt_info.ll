define fun main:
	i_2 = alloc: i32
	i_2: i32 = 0
	if false 1 goto  $L0
	a_3 = alloc: i32
	goto  $L1

$L0:
	if false 2 goto  $L2
	b_4 = alloc: i32
	goto  $L3

$L2:
	c_5 = alloc: i32

$L3:

$L1:
	ret: i32  0