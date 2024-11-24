define fun func:
	ret: i32  335

define fun main:
	a_3 = alloc: i32
	%1 = call: i8  func
	a_3: i32 = %1
	%2: i32 = a_3
	ret: i32  %2