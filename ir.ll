define fun main:
	a_3 = alloc: i32
	a_3: i32 = 1
	%1: i32 = a_3
	if false %1 goto  $L0_if_end
	b_4 = alloc: i32  2
	b_4[0]: i32 = 2
	b_4[1]: i32 = 4

$L1_for_cond:
	%2: i32 = b_4[1]
	%3: i32 = %2 <= 6
	if false %3 goto  $L3_for_end
	%4: i32 = b_4[1]
	param: i32  %4
	call: void  putint

$L2_for_motion:
	%5: i32 = b_4[1]
	%6: i32 = %5 + 1
	b_4[1]: i32 = %6
	goto  $L1_for_cond

$L3_for_end:

$L0_if_end:
	ret: i32  0