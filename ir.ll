fun main:
	@2.a = alloc: i32  2
	@2.a[0]: i32 = 0
	@2.a[1]: i32 = 0
	%1: i32 = @2.a[1]
	ret: i32  %1