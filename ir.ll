define fun main:
	a_2 = alloc: i32
	a_2: i32 = 1
	b_2 = alloc: i32
	b_2: i32 = a_2
	c_2 = alloc: i32
	c_2: i32 = b_2
	d_2 = alloc: i32
	d_2: i32 = c_2
	ret: i32  d_2