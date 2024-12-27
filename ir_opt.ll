define fun main:
	%1: i32 = 1
	if false %1 goto  $L0
	param: i32  4
	call: void  putint

$L0:
	ret: i32  0