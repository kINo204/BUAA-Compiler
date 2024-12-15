global fib_B0_0: i32 = 0
global fib_B1_0: i32 = 0
global fib_B2_0: i32 = 0
global fib_B3_0: i32 = 0
global fib_B4_0: i32 = 0
global main_B0_0: i32 = 0
global main_B1_0: i32 = 0
global main_B2_0: i32 = 0
global main_B3_0: i32 = 0

global N_1: i32 = 10

global a_1[10]: i32 = 0, 1, 2, 3, 4, 5, 6, 7, 8, 9

define fun fib:
	%55: i32 = fib_B0_0 + 1
	fib_B0_0: i32 = %55
	%1: i32 = i_2 == 1
	if false %1 goto  $L0
	%56: i32 = fib_B1_0 + 1
	fib_B1_0: i32 = %56
	ret: i32  1

$L0:
	%57: i32 = fib_B2_0 + 1
	fib_B2_0: i32 = %57
	%2: i32 = i_2 == 2
	if false %2 goto  $L1
	%58: i32 = fib_B3_0 + 1
	fib_B3_0: i32 = %58
	ret: i32  2

$L1:
	%59: i32 = fib_B4_0 + 1
	fib_B4_0: i32 = %59
	%3: i32 = i_2 - 1
	param: i32  %3
	%4 = call: i32  fib
	%5: i32 = i_2 - 2
	param: i32  %5
	%6 = call: i32  fib
	%7: i32 = %4 + %6
	ret: i32  %7

define fun main:
	%60: i32 = main_B0_0 + 1
	main_B0_0: i32 = %60
	i_5 = alloc: i32
	j_5 = alloc: i32
	%1 = call: i32  getint
	i_5: i32 = %1
	%2 = call: i32  getint
	j_5: i32 = %2
	%3: i32 = i_5 * j_5
	%4: i32 = 0 - %3
	param: i32  4
	%5 = call: i32  fib
	%6: i32 = %4 * %5
	%7: i32 = a_1[1]
	%8: i32 = %7
	%10: i32 = %6
	%11: i32 = %10 + %8
	%12: i32 = %11
	%13: i32 = %12 / 5
	i_5: i32 = %13
	param: i32  5
	%17 = call: i32  fib
	%18: i32 = %17 + 2
	param: i32  %18
	%19 = call: i32  fib
	%20: i32 = 1197 - %19
	%21: i32 = %20 + -10091
	j_5: i32 = %21
	k_5 = alloc: i32
	k_5: i32 = -6
	%22: i32 = a_1[0]
	%23: i32 = i_5 * i_5
	%24: i32 = %22 + %23
	a_1[0]: i32 = %24
	%25: i32 = a_1[1]
	%26: i32 = i_5 * i_5
	%27: i32 = %25 + %26
	a_1[1]: i32 = %27
	%28: i32 = a_1[2]
	%29: i32 = i_5 * i_5
	%30: i32 = %28 + %29
	a_1[2]: i32 = %30
	%31: i32 = a_1[3]
	%32: i32 = i_5 * i_5
	%33: i32 = %31 + %32
	a_1[3]: i32 = %33
	%34: i32 = a_1[4]
	%35: i32 = i_5 * i_5
	%36: i32 = %34 + %35
	a_1[4]: i32 = %36
	%37: i32 = a_1[5]
	%38: i32 = i_5 * i_5
	%39: i32 = %37 + %38
	a_1[5]: i32 = %39
	%40: i32 = a_1[6]
	%41: i32 = i_5 * i_5
	%42: i32 = %40 + %41
	a_1[6]: i32 = %42
	%43: i32 = a_1[7]
	%44: i32 = i_5 * i_5
	%45: i32 = %43 + %44
	a_1[7]: i32 = %45
	%46: i32 = a_1[8]
	%47: i32 = i_5 * i_5
	%48: i32 = %46 + %47
	a_1[8]: i32 = %48
	%49: i32 = a_1[9]
	%50: i32 = i_5 * i_5
	%51: i32 = %49 + %50
	a_1[9]: i32 = %51
	i_5: i32 = 0

$L3:
	%61: i32 = main_B1_0 + 1
	main_B1_0: i32 = %61
	%52: i32 = i_5 < 10
	if false %52 goto  $L2
	%62: i32 = main_B2_0 + 1
	main_B2_0: i32 = %62
	%53: i32 = a_1[i_5]
	param: i32  %53
	call: void  putint
	param: i8  44
	call: void  putchar
	param: i8  32
	call: void  putchar
	%54: i32 = i_5 + 1
	i_5: i32 = %54
	goto  $L3

$L2:
	%63: i32 = main_B3_0 + 1
	main_B3_0: i32 = %63
	param: i8  10
	call: void  putchar
	param: i32  i_5
	call: void  putint
	param: i8  44
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i32  j_5
	call: void  putint
	param: i8  44
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i32  k_5
	call: void  putint
	param: i8  10
	call: void  putchar
	param: i8  10
	call: void  putchar
	param: i8  80
	call: void  putchar
	param: i8  114
	call: void  putchar
	param: i8  111
	call: void  putchar
	param: i8  102
	call: void  putchar
	param: i8  105
	call: void  putchar
	param: i8  108
	call: void  putchar
	param: i8  101
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i8  105
	call: void  putchar
	param: i8  110
	call: void  putchar
	param: i8  102
	call: void  putchar
	param: i8  111
	call: void  putchar
	param: i8  58
	call: void  putchar
	param: i8  10
	call: void  putchar
	param: i8  102
	call: void  putchar
	param: i8  105
	call: void  putchar
	param: i8  98
	call: void  putchar
	param: i8  95
	call: void  putchar
	param: i8  66
	call: void  putchar
	param: i8  48
	call: void  putchar
	param: i8  9
	call: void  putchar
	param: i32  fib_b0_0
	call: void  putint
	param: i8  10
	call: void  putchar
	param: i8  102
	call: void  putchar
	param: i8  105
	call: void  putchar
	param: i8  98
	call: void  putchar
	param: i8  95
	call: void  putchar
	param: i8  66
	call: void  putchar
	param: i8  49
	call: void  putchar
	param: i8  9
	call: void  putchar
	param: i32  fib_b1_0
	call: void  putint
	param: i8  10
	call: void  putchar
	param: i8  102
	call: void  putchar
	param: i8  105
	call: void  putchar
	param: i8  98
	call: void  putchar
	param: i8  95
	call: void  putchar
	param: i8  66
	call: void  putchar
	param: i8  50
	call: void  putchar
	param: i8  9
	call: void  putchar
	param: i32  fib_b2_0
	call: void  putint
	param: i8  10
	call: void  putchar
	param: i8  102
	call: void  putchar
	param: i8  105
	call: void  putchar
	param: i8  98
	call: void  putchar
	param: i8  95
	call: void  putchar
	param: i8  66
	call: void  putchar
	param: i8  51
	call: void  putchar
	param: i8  9
	call: void  putchar
	param: i32  fib_b3_0
	call: void  putint
	param: i8  10
	call: void  putchar
	param: i8  102
	call: void  putchar
	param: i8  105
	call: void  putchar
	param: i8  98
	call: void  putchar
	param: i8  95
	call: void  putchar
	param: i8  66
	call: void  putchar
	param: i8  52
	call: void  putchar
	param: i8  9
	call: void  putchar
	param: i32  fib_b4_0
	call: void  putint
	param: i8  10
	call: void  putchar
	param: i8  109
	call: void  putchar
	param: i8  97
	call: void  putchar
	param: i8  105
	call: void  putchar
	param: i8  110
	call: void  putchar
	param: i8  95
	call: void  putchar
	param: i8  66
	call: void  putchar
	param: i8  48
	call: void  putchar
	param: i8  9
	call: void  putchar
	param: i32  main_b0_0
	call: void  putint
	param: i8  10
	call: void  putchar
	param: i8  109
	call: void  putchar
	param: i8  97
	call: void  putchar
	param: i8  105
	call: void  putchar
	param: i8  110
	call: void  putchar
	param: i8  95
	call: void  putchar
	param: i8  66
	call: void  putchar
	param: i8  49
	call: void  putchar
	param: i8  9
	call: void  putchar
	param: i32  main_b1_0
	call: void  putint
	param: i8  10
	call: void  putchar
	param: i8  109
	call: void  putchar
	param: i8  97
	call: void  putchar
	param: i8  105
	call: void  putchar
	param: i8  110
	call: void  putchar
	param: i8  95
	call: void  putchar
	param: i8  66
	call: void  putchar
	param: i8  50
	call: void  putchar
	param: i8  9
	call: void  putchar
	param: i32  main_b2_0
	call: void  putint
	param: i8  10
	call: void  putchar
	param: i8  109
	call: void  putchar
	param: i8  97
	call: void  putchar
	param: i8  105
	call: void  putchar
	param: i8  110
	call: void  putchar
	param: i8  95
	call: void  putchar
	param: i8  66
	call: void  putchar
	param: i8  51
	call: void  putchar
	param: i8  9
	call: void  putchar
	param: i32  main_b3_0
	call: void  putint
	param: i8  10
	call: void  putchar
	ret: i32  0