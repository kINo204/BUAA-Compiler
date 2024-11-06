fun main:
	@2.i = alloc: i32
	if 1 goto $L2_lorexp-true
	if 0 goto $L2_lorexp-true
	%1: i32 = 0
	goto  $l3_lorexp-end

$L2_lorexp-true:
	%1: i32 = 1

$L3_lorexp-end:
	if not %1 goto $L0_if-else
	goto  $l1_if-end

$L0_if-else:

$L1_if-end:
	@2.i: i32 = 0

$L4_for-start:
	%2: i32 = @2.i
	%3 = lss: i32  %2, 10
	if not %3 goto $L5_for-end
	%4 = add: i32  5, 5
	@2.i: i32 = 1

$L6_for-start:
	%5: i32 = @2.i
	%6 = lss: i32  %5, 20
	if not %6 goto $L7_for-end
	goto  $l7_for-end
	goto  $l6_for-start
	%7: i32 = @2.i
	%8 = add: i32  %7, 1
	@2.i: i32 = %8
	goto  $l6_for-start

$L7_for-end:
	goto  $l5_for-end
	goto  $l4_for-start
	%9: i32 = @2.i
	%10 = add: i32  %9, 1
	@2.i: i32 = %10
	goto  $l4_for-start

$L5_for-end:
	ret: i32  0