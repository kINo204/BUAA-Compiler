define fun main:
	a_2 = alloc: i32
	a_2: i32 = 1
	a_3 = alloc: i32
	a_3: i32 = 2
	a_4 = alloc: i32
	a_4: i32 = 3
	%1: i32 = a_4
	ret: i32  %1
	ret: i32  0