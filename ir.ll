fun @1.main:
	@2.i = alloc: i32
	@2.res = alloc: i32
	@2.res: i32 = 0
	@2.i: i32 = 1

$l0_for_start:
	%1: i32 = @2.res
	%2: i32 = @2.i
	%3 = add: i32  %1, %2
	@2.res: i32 = %3
	%4: i32 = @2.i
	%5 = add: i32  %4, 1
	@2.i: i32 = %5
	goto  $l0_for_start

$l1_for_end:
	%6: i32 = @2.res
	ret: i32  %6