fun main:
	@2.i = alloc: i32
	@2.i: i32 = 0
	label  _0_for-start
	%1: i32 = @2.i
	%2 = lss: i32  %1, 10
	gont  _1_for-end, %2
	%3 = add: i32  5, 5
	@2.i: i32 = 1
	label  _2_for-start
	%4: i32 = @2.i
	%5 = lss: i32  %4, 20
	gont  _3_for-end, %5
	goto  _3_for-end
	goto  _2_for-start
	%6: i32 = @2.i
	%7 = add: i32  %6, 1
	@2.i: i32 = %7
	goto  _2_for-start
	label  _3_for-end
	goto  _1_for-end
	goto  _0_for-start
	%8: i32 = @2.i
	%9 = add: i32  %8, 1
	@2.i: i32 = %9
	goto  _0_for-start
	label  _1_for-end
	ret: i32  0