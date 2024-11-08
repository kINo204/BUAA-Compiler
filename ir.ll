global ci0_1: i32 = 1

global ci1_1: i32 = 2

global ci2_1: i32 = 4

global cc0_1: i8 = 49

global cc1_1: i8 = 50

global cc2_1: i8 = 52

global vi0_1: i32 = 1

global vi1_1: i32 = 2

global vi2_1: i32 = 4

global vc0_1: i8 = 49

global vc1_1: i8 = 50

global vc2_1: i8 = 52

fun add:
	%1: i32 = a_2
	%2: i8 = b_2
	%3 = add: i32  %1, %2
	ret: i32  %3

fun main:
	param  50
	call: void  putchar
	param  50
	call: void  putchar
	param  51
	call: void  putchar
	param  55
	call: void  putchar
	param  49
	call: void  putchar
	param  50
	call: void  putchar
	param  56
	call: void  putchar
	param  49
	call: void  putchar
	param  10
	call: void  putchar
	param  101
	call: void  putchar
	param  114
	call: void  putchar
	param  114
	call: void  putchar
	param  32
	call: void  putchar
	param  99
	call: void  putchar
	param  97
	call: void  putchar
	param  117
	call: void  putchar
	param  103
	call: void  putchar
	param  104
	call: void  putchar
	param  116
	call: void  putchar
	param  58
	call: void  putchar
	param  32
	call: void  putchar
	e_c1_3 = alloc: i8
	e_c1_3: i8 = 0
	e_c2_3 = alloc: i8
	e_c2_3: i8 = 0
	e_c3_3 = alloc: i8
	e_c3_3: i8 = 0
	e_c4_3 = alloc: i8
	e_c4_3: i8 = 0
	e_c5_3 = alloc: i8
	e_c5_3: i8 = 0
	e_c6_3 = alloc: i8
	e_c6_3: i8 = 0
	e_c7_3 = alloc: i8
	e_c7_3: i8 = 0
	e_c8_3 = alloc: i8
	e_c8_3: i8 = 0
	e_c9_3 = alloc: i8
	e_c9_3: i8 = 0
	e_c10_3 = alloc: i8
	e_c10_3: i8 = 0
	e_c11_3 = alloc: i8
	e_c11_3: i8 = 0
	e_c12_3 = alloc: i8
	e_c12_3: i8 = 0
	e_c13_3 = alloc: i8
	e_c13_3: i8 = 0
	e_c14_3 = alloc: i8
	e_c14_3: i8 = 0
	e_c15_3 = alloc: i8
	e_c15_3: i8 = 0
	e_c16_3 = alloc: i8
	e_c16_3: i8 = 0
	%1: i32 = ci0_1
	%2: i32 = ci1_1
	%3: i32 = ci2_1
	%4 = add: i32  %1, %2
	%5 = add: i32  %4, %3
	%6 = neq: i32  %5, 7
	if not %6 goto  $L0_if_end
	e_c1_3: i8 = 49

$L0_if_end:
	%7: i8 = cc0_1
	%8: i8 = cc1_1
	%9 = sub: i32  %8, 48
	%10: i8 = cc2_1
	%11 = sub: i32  %10, 48
	%12 = add: i32  %7, %9
	%13 = add: i32  %12, %11
	%14 = neq: i32  %13, 55
	if not %14 goto  $L1_if_end
	e_c2_3: i8 = 50

$L1_if_end:
	%15: i32 = vi0_1
	%16: i32 = vi1_1
	%17: i32 = vi2_1
	%18 = add: i32  %15, %16
	%19 = add: i32  %18, %17
	%20 = neq: i32  %19, 7
	if not %20 goto  $L2_if_end
	e_c3_3: i8 = 51

$L2_if_end:
	%21: i8 = vc0_1
	%22: i8 = vc1_1
	%23 = sub: i32  %22, 48
	%24: i8 = vc2_1
	%25 = sub: i32  %24, 48
	%26 = add: i32  %21, %23
	%27 = add: i32  %26, %25
	%28 = neq: i32  %27, 55
	if not %28 goto  $L3_if_end
	e_c4_3: i8 = 52

$L3_if_end:
	c1_3 = alloc: i32
	param  1
	param  48
	%29 = call: i32  add
	c1_3: i32 = %29
	%30: i32 = c1_3
	%31 = neq: i32  %30, 49
	if not %31 goto  $L4_if_end
	e_c5_3: i8 = 53

$L4_if_end:
	b0_3 = alloc: i32
	b0_3: i32 = 0
	b0_6 = alloc: i32
	b0_6: i32 = 5
	%32: i32 = b0_6
	%33 = neq: i32  %32, 5
	if not %33 goto  $L5_if_end
	e_c6_3: i8 = 54

$L5_if_end:
	%34: i32 = b0_3
	%35 = neq: i32  %34, 0
	if not %35 goto  $L6_if_end
	e_c6_3: i8 = 54

$L6_if_end:
	%36: i8 = e_c1_3
	param  %36
	call: void  putchar
	%37: i8 = e_c2_3
	param  %37
	call: void  putchar
	%38: i8 = e_c3_3
	param  %38
	call: void  putchar
	%39: i8 = e_c4_3
	param  %39
	call: void  putchar
	%40: i8 = e_c5_3
	param  %40
	call: void  putchar
	%41: i8 = e_c6_3
	param  %41
	call: void  putchar
	%42: i8 = e_c7_3
	param  %42
	call: void  putchar
	%43: i8 = e_c8_3
	param  %43
	call: void  putchar
	%44: i8 = e_c9_3
	param  %44
	call: void  putchar
	%45: i8 = e_c10_3
	param  %45
	call: void  putchar
	%46: i8 = e_c11_3
	param  %46
	call: void  putchar
	%47: i8 = e_c12_3
	param  %47
	call: void  putchar
	%48: i8 = e_c13_3
	param  %48
	call: void  putchar
	%49: i8 = e_c14_3
	param  %49
	call: void  putchar
	%50: i8 = e_c15_3
	param  %50
	call: void  putchar
	%51: i8 = e_c16_3
	param  %51
	call: void  putchar
	param  10
	call: void  putchar
	ret: i32  0