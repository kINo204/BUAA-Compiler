fun main:
	@2.i = alloc: i32
	@2.i: i32 = 0
	label  _0_for-start
	%1: i32 = @2.i
	%2 = lss: i32  %1, 10
	gont  _1_for-end, %2
	%3 = add: i32  5, 5
	%4: i32 = @2.i
	%5 = add: i32  %4, 1
	@2.i: i32 = %5
	goto  _0_for-start
	label  _1_for-end
	ret: i32  0