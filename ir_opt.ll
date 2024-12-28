define fun main:
	%1: i32 = 1
	if false %1 goto  $L0
	b_4 = alloc: i32  2
	b_4[0]: i32 = 2
	b_4[1]: i32 = 4

$L1:
	%2: i32 = b_4[1]
	%3: i32 = %2 <= 6
	if false %3 goto  $L0
	%4: i32 = b_4[1]
	param: i32  %4
	call: void  putint
	%5: i32 = b_4[1]
	%6: i32 = %5 + 1
	b_4[1]: i32 = %6
	goto  $L1

$L0:
	ret: i32  0