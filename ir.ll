define fun main:
	a_3 = alloc: i32
	a_3: i32 = 1
	%1: i32 = a_3
	if false %1 goto  $L0_if_end
	b_4 = alloc: i32
	b_4: i32 = 4
	param: i32  b_4
	call: void  putint

$L0_if_end:
	ret: i32  0