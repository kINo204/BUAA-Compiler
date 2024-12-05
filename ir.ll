define fun func:
	%1 = eql: i32  n_2, 0
	if false %1 goto  $L0_if_end
	ret: i32  n_2

$L0_if_end:
	%2 = sub: i32  n_2, 1
	n_2: i32 = %2
	param: i32  n_2
	%3 = call: i32  func
	ret: i32  %3

define fun main:
	a_3 = alloc: i32
	a_3: i32 = 5
	param: i32  a_3
	%1 = call: i32  func
	ret: i32  a_3