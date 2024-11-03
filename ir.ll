fun main:
	%1 = sub: i32  0, 2
	%2 = add: i32  1, %1
	%3 = eql: i32  %2, 0
	%4 = sub: i32  0, 3
	%5 = mul: i32  %3, %4
	%6 = eql: i32  '4', 0
	%7 = add: i32  %5, %6
	ret: i32  %7