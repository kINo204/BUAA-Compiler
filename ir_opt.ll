define fun func:

$L1:
	%1 = eql: i32  n_2, 0
	if false %1 goto  $L0
	ret: i32  n_2

$L0:
	%2 = sub: i32  n_2, 1
	n_2: i32 = %2
	goto  $L1

define fun main:
	param: i32  4
	%1 = call: i32  func
	ret: i32  0