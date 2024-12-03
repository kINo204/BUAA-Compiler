
global N_1: i32 = 10

global a_1[10]: i32 = 0, 1, 2, 3, 4, 5, 6, 7, 8, 9

define fun fib:
	%1 = eql: i32  i_2, 1
	if false %1 goto  $L0
	ret: i32  1

$L0:
	%2 = eql: i32  i_2, 2
	if false %2 goto  $L1
	ret: i32  2

$L1:
	%3 = sub: i32  i_2, 1
	param: i32  %3
	%4 = call: i32  fib
	%5 = sub: i32  i_2, 2
	param: i32  %5
	%6 = call: i32  fib
	%7 = add: i32  %4, %6
	ret: i32  %7

define fun main:
	i_5 = alloc: i32
	i_5: i32 = 2
	j_5 = alloc: i32
	j_5: i32 = 5
	a1_5 = alloc: i32
	a1_5: i32 = 1
	a2_5 = alloc: i32
	a2_5: i32 = 2
	%1 = call: i32  getint
	i_5: i32 = %1
	%2 = call: i32  getint
	j_5: i32 = %2
	%3 = mul: i32  i_5, j_5
	%4 = sub: i32  0, %3
	param: i32  4
	%5 = call: i32  fib
	%6 = mul: i32  %4, %5
	%7: i32 = a_1[1]
	%8 = mul: i32  %7, 1
	%9 = div: i32  1, 2
	%10 = add: i32  %6, 0
	%11 = add: i32  %10, %8
	%12 = sub: i32  %11, %9
	%13 = div: i32  %12, 5
	i_5: i32 = %13
	%14 = mul: i32  7, 5923
	%15 = mod: i32  %14, 56
	%16 = mul: i32  %15, 57
	param: i32  5
	%17 = call: i32  fib
	%18 = add: i32  %17, 2
	param: i32  %18
	%19 = call: i32  fib
	%20 = sub: i32  %16, %19
	%21 = add: i32  %20, -10091
	j_5: i32 = %21
	k_5 = alloc: i32
	k_5: i32 = -6
	%22: i32 = a_1[0]
	%23 = mul: i32  i_5, i_5
	%24 = add: i32  %22, %23
	a_1[0]: i32 = %24
	%25: i32 = a_1[1]
	%26 = mul: i32  i_5, i_5
	%27 = add: i32  %25, %26
	a_1[1]: i32 = %27
	%28: i32 = a_1[2]
	%29 = mul: i32  i_5, i_5
	%30 = add: i32  %28, %29
	a_1[2]: i32 = %30
	%31: i32 = a_1[3]
	%32 = mul: i32  i_5, i_5
	%33 = add: i32  %31, %32
	a_1[3]: i32 = %33
	%34: i32 = a_1[4]
	%35 = mul: i32  i_5, i_5
	%36 = add: i32  %34, %35
	a_1[4]: i32 = %36
	%37: i32 = a_1[5]
	%38 = mul: i32  i_5, i_5
	%39 = add: i32  %37, %38
	a_1[5]: i32 = %39
	%40: i32 = a_1[6]
	%41 = mul: i32  i_5, i_5
	%42 = add: i32  %40, %41
	a_1[6]: i32 = %42
	%43: i32 = a_1[7]
	%44 = mul: i32  i_5, i_5
	%45 = add: i32  %43, %44
	a_1[7]: i32 = %45
	%46: i32 = a_1[8]
	%47 = mul: i32  i_5, i_5
	%48 = add: i32  %46, %47
	a_1[8]: i32 = %48
	%49: i32 = a_1[9]
	%50 = mul: i32  i_5, i_5
	%51 = add: i32  %49, %50
	a_1[9]: i32 = %51
	i_5: i32 = 0

$L3:
	%52 = lss: i32  i_5, 10
	if false %52 goto  $L2
	%53: i32 = a_1[i_5]
	param: i32  %53
	call: void  putint
	param: i8  44
	call: void  putchar
	param: i8  32
	call: void  putchar
	%54 = add: i32  i_5, 1
	i_5: i32 = %54
	goto  $L3

$L2:
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
	ret: i32  0