fun test:
	%1: i8 = *(@2.p1)[1]
	ret: i8  %1

fun main:
	@3.arr = alloc: i8  2
	@3.arr[0]: i8 = 'A'
	@3.arr[1]: i8 = 'C'
	%2: &i32 = &(@3.arr)
	param  %2
	%3 = call: i8  test
	ret: i8  %3