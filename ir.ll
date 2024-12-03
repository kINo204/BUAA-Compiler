define fun main:
	a_2 = alloc: i32  1
	a_2[0]: i32 = 2
	v_2 = alloc: i32
	%1: i32 = a_2[0]
	v_2: i32 = %1
	ret: i32  v_2