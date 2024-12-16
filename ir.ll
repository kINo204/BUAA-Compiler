global x_1: i32 = 4

define fun main:
	a_2 = alloc: i32
	b_2 = alloc: i32
	b_2: i32 = x_1
	a_2: i32 = b_2
	ret: i32  a_2