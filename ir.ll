fun @1.int_func:
	ret: i32  0

fun @1.char_func:
	ret: i8  'r'

fun @1.void_func:

fun @1.main:
	param  1
	%1 = call: void  @1.void_func
	param  2
	%2 = call: i32  @1.int_func
	param  3
	%3 = call: i8  @1.char_func
	ret: i8  %3