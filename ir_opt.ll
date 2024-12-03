define fun main:
	blc_0_0 = alloc: i32
	BLC_0_0: i32 = 0
	%2 = add: i32  blc_0_0, 1
	BLC_0_0: i32 = %2
	a_2 = alloc: i32  1
	a_2[0]: i32 = 2
	v_2 = alloc: i32
	%1: i32 = a_2[0]
	v_2: i32 = %1
	param: i8  66
	call: void  putchar
	param: i32  0
	call: void  putint
	param: i8  58
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i32  blc_0_0
	call: void  putint
	param: i8  10
	call: void  putchar
	ret: i32  v_2