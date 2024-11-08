define fun main:
	a_2 = alloc: i32
	if false 1 goto  $L0_if_end
	a_2: i32 = 1

$L0_if_end:
	a_2: i32 = 2
	ret: i32  0