define fun f:
	%1: i32 = x_2 + 1
	ret: i32  %1

define fun g:
	param: i32  x_3
	%1 = call: i32  f
	%2: i32 = x_3 / %1
	ret: i32  %2

define fun main:
	x_4 = alloc: i32
	n_4 = alloc: i32
	m_4 = alloc: i32
	param: i32  n_4
	%1 = call: i32  g
	x_4: i32 = %1
	param: i32  x_4
	%2 = call: i32  g
	m_4: i32 = %2
	ret: i32  0