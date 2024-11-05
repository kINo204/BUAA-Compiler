fun main:
	@2.i = alloc: i32
	@2.i: i32 = 0

$L0_for-start:
	%1: i32 = @2.i
	%2 = lss: i32  %1, 10
	gont  $l1_for-end, %2
	%3 = add: i32  5, 5
	@2.i: i32 = 1

$L2_for-start:
	%4: i32 = @2.i
	%5 = lss: i32  %4, 20
	gont  $l3_for-end, %5
	goto  $l3_for-end
	goto  $l2_for-start
	%6: i32 = @2.i
	%7 = add: i32  %6, 1
	@2.i: i32 = %7
	goto  $l2_for-start

$L3_for-end:
	goto  $l1_for-end
	goto  $l0_for-start
	%8: i32 = @2.i
	%9 = add: i32  %8, 1
	@2.i: i32 = %9
	goto  $l0_for-start

$L1_for-end:
	ret: i32  0