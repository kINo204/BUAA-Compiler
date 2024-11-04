fun main:
	@2.a = alloc: i32
	@2.a: i32 = 1
	@2.b = alloc: i32  2
	@2.b[0]: i32 = 1
	@2.c = alloc: i8
	@2.c: i8 = 'x'
	@2.d = alloc: i8  3
	@2.d[0]: i8 = 'y'
	@2.d[1]: i8 = 0
	@2.d[2]: i8 = 0
	ret: i32  0