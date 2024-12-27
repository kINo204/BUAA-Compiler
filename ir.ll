define fun main:
	a_3 = alloc: i32
	a_3: i32 = 1
	%1: i32 = a_3
	if false %1 goto  $L0_if_end
	b_4 = alloc: i32
	b_4: i32 = 4
	param: i32  b_4
	call: void  putint
	b_5 = alloc: i32
	b_5: i32 = 3
	%2: i32 = b_5
	if false %2 goto  $L1_if_end
	param: i32  b_5
	call: void  putint
	c_6 = alloc: i8
	%3 = call: i8  getchar
	c_6: i8 = %3
	param: i8  c_6
	call: void  putchar

$L1_if_end:

$L0_if_end:
	ret: i32  0