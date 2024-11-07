fun count:
	@2.res = alloc: i32
	@2.res: i32 = 0
	@2.i = alloc: i32
	@2.i: i32 = 1

$L0_for_cond:
	%1: i32 = @2.i
	%2: i32 = @2.max
	%3 = gre: i32  %1, %2
	if not %3 goto  $L3_if_else
	goto  $L2_for_end
	goto  $L4_if_end

$L3_if_else:
	%4: i32 = @2.i
	%5: i32 = @2.max
	%6 = leq: i32  %4, %5
	if not %6 goto  $L5_if_end
	%7: i32 = @2.res
	%8: i32 = @2.i
	%9 = add: i32  %7, %8
	@2.res: i32 = %9
	goto  $L1_for_motion

$L5_if_end:

$L4_if_end:

$L1_for_motion:
	%10: i32 = @2.i
	%11 = add: i32  %10, 1
	@2.i: i32 = %11
	goto  $L0_for_cond

$L2_for_end:
	%12: i32 = @2.res
	ret: i32  %12

fun main:
	@6.sum = alloc: i32
	param  100
	%1 = call: i32  count
	@6.sum: i32 = %1
	%2: i32 = @6.sum
	ret: i32  %2