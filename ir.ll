define fun main:
	a_2 = alloc: i32
	a_2: i32 = 0
	b_2 = alloc: i32  2
	b_2[0]: i32 = 0
	b_2[1]: i32 = 0
	%1: i32 = a_2
	%2: i32 = b_2[%1]
	param: i8  %2
	call: void  putchar
	ret: i32  0