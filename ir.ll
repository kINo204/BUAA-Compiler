global a_1: i32 = 1

global b_1[2]: i32 = 2, 3

global c_1: i8 = 65

global d_1[3]: i8 = 66, 67, 0

global a1_1: i32 = 0

global b1_1[2]: i32 = 0, 0

global c1_1: i8 = 0

global d1_1[3]: i8 = 0, 0, 0

fun main:
	%1: i8 = d_1[2]
	ret: i8  %1