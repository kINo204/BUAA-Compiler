fun test:
	%1: i32 = @2.p1
	%2: i8 = @2.p2
	%3: i8 = @2.p3
	%4 = add: i32  %1, %2
	%5 = add: i32  %4, %3
	ret: i32  %5

fun main:
	param  1
	param  'a'
	param  'a'
	%1 = call: i32  test
	ret: i32  %1