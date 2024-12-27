define fun main:
	%1: i32 = 1
	if false %1 goto  $L0
	param: i32  4
	call: void  putint
	%2: i32 = 3
	if false %2 goto  $L0
	param: i32  3
	call: void  putint
	c_6 = alloc: i8
	%3 = call: i8  getchar
	c_6: i8 = %3
	param: i8  c_6
	call: void  putchar

$L0:
	ret: i32  0