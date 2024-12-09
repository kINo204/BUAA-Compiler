define fun main:
	a_2 = alloc: i32
	a_2: i32 = 10
	%1: i32 = a_2
	if false %1 goto  $L0
	a_2: i32 = 1

$L0:
	ret: i32  a_2