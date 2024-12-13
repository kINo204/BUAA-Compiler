define fun f:
	*(a_2)[0]: i32 = 1
	b_2 = alloc: i32
	%1: i32 = *(a_2)[0]
	b_2: i32 = %1
	ret

define fun main:
	a_3 = alloc: i32  2
	a_3[0]: i32 = 1
	%1: &i32 = &(a_3)
	param: i32  %1
	call: void  f
	b_3 = alloc: i32
	%2: i32 = a_3[0]
	b_3: i32 = %2
	c_3 = alloc: i32
	c_3: i32 = b_3
	ret: i32  c_3