define fun main:
	a_2 = alloc: i32
	a_2: i32 = 1
	b_2 = alloc: i32
	%1 = add: i32  a_2, 2
	b_2: i32 = %1
	ret: i32  b_2