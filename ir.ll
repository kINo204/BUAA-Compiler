fun test:
	%1: i32 = @2.p1
	ret: i32  %1

fun main:
	@3.arr = alloc: i32  2
	@3.arr[0]: i32 = 3
	@3.arr[1]: i32 = 4
	%1: i32 = @3.arr[0]
	param  %1
	%2 = call: i32  test
	ret: i32  %2