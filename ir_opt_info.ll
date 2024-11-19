global const_var1_1: i32 = 17

global const_var2_1: i32 = 3

global arr_1[3]: i32 = 1, 2, 3

global const_var3_1: i8 = 99

global s_1[5]: i8 = 97, 98, 99, 100, 0

global str_1[5]: i8 = 97, 98, 99, 100, 0

global cot_var1_1: i32 = 20

global var1_1: i32 = 5

global var2_1: i32 = 2

global var3_1: i32 = 10

global var4_1: i8 = 7

global var5_1: i8 = 8

define fun f3:
	i_2 = alloc: i32
	i_2: i32 = 0

$L1:
	%1: i32 = i_2
	%2 = eql: i32  %1, 0
	if false %2 goto  $L0
	%3: i32 = para1_2
	%4: i32 = para2_2
	%5 = add: i32  %3, %4
	para1_2: i32 = %5
	%6: i32 = i_2
	%7 = add: i32  %6, 1
	i_2: i32 = %7
	goto  $L1

$L0:
	%8: i32 = i_2
	%9: i32 = *(arr1_2)[%8]
	%10: i32 = i_2
	%11: i32 = *(arr2_2)[%10]
	%12: i32 = para1_2
	%13 = add: i32  %11, %12
	%14 = mul: i32  %9, %13
	%15: i32 = para2_2
	%16 = sub: i32  %14, %15
	ret: i32  %16

define fun f4:
	%1: i32 = para1_4
	%2: i32 = para2_4
	%3 = add: i32  %1, %2
	ret: i32  %3

define fun f5:
	%1: i8 = para1_5
	%2: i8 = para2_5
	%3 = add: i32  %1, %2
	ret: i32  %3

define fun f7:
	%1: i32 = para_6
	ret: i32  %1

define fun f8:
	%1: i8 = para_7
	ret: i8  %1

define fun f6:
	ret: i32  0

define fun f2:
	%1: i32 = para_9
	param: i32  %1
	call: void  putint
	param: i8  10
	call: void  putchar
	ret

define fun f9:
	ret

define fun main:
	param: i8  50
	call: void  putchar
	param: i8  50
	call: void  putchar
	param: i8  51
	call: void  putchar
	param: i8  55
	call: void  putchar
	param: i8  49
	call: void  putchar
	param: i8  52
	call: void  putchar
	param: i8  57
	call: void  putchar
	param: i8  49
	call: void  putchar
	param: i8  10
	call: void  putchar
	a_11 = alloc: i32
	a_11: i32 = 0
	call: void  f9
	ff_11 = alloc: i32
	ff_11: i32 = 1
	arr_11 = alloc: i32  3
	arr1_11 = alloc: i32  3
	arr1_11[0]: i32 = 1
	arr1_11[1]: i32 = 2
	arr1_11[2]: i32 = 3
	arr2_11 = alloc: i32  3
	arr2_11[0]: i32 = 4
	arr2_11[1]: i32 = 5
	arr2_11[2]: i32 = 6
	o_11 = alloc: i32
	%1: &i32 = &(arr1_11)
	%2: &i32 = &(arr2_11)
	%3: i32 = a_11
	%4: i32 = ff_11
	param: i32  %1
	param: i32  %2
	param: i32  %3
	param: i32  %4
	%5 = call: i32  f3
	o_11: i32 = %5
	b_11 = alloc: i32
	b_11: i32 = 1
	e_11 = alloc: i8
	e_11: i8 = 99
	param: i8  99
	%6 = call: i8  f8
	e_11: i8 = %6
	%7: i32 = a_11
	%8: i32 = b_11
	%9 = add: i32  %7, %8
	%10: i32 = b_11
	%11 = add: i32  %9, %10
	%12 = add: i32  %11, 10
	a_11: i32 = %12
	%13: i32 = a_11
	%14 = sub: i32  0, %13
	a_11: i32 = %14
	%15: i32 = a_11
	%16 = sub: i32  0, %15
	%17: i32 = a_11
	%18: i32 = a_11
	%19 = div: i32  %18, 2
	a_11: i32 = %19
	%20: i32 = a_11
	%21 = mod: i32  %20, 2
	a_11: i32 = %21
	%22: i32 = a_11
	%23 = eql: i32  %22, 0
	if false %23 goto  $L2

$L2:
	param: i8  103
	param: i8  102
	%24 = call: i8  f5
	e_11: i8 = %24
	h_11 = alloc: i32
	%25: i32 = a_11
	%26 = add: i32  %25, 1
	h_11: i32 = %26
	%27: i32 = a_11
	h_11: i32 = %27
	%28 = call: i8  getchar
	e_11: i8 = %28
	d_11 = alloc: i32
	d_11: i32 = 4
	c_11 = alloc: i32
	c_11: i32 = 1
	i_11 = alloc: i32
	param: i32  0
	%29 = call: i32  f7
	i_11: i32 = %29
	%30 = call: i32  getint
	c_11: i32 = %30
	%31: i32 = a_11
	%32: i32 = d_11
	param: i32  %31
	param: i32  %32
	%33 = call: i32  f4
	c_11: i32 = %33
	i_11: i32 = 0

$L6:
	%34: i32 = i_11
	%35: i32 = d_11
	%36 = lss: i32  %34, %35
	if false %36 goto  $L3
	%37: i32 = a_11
	%38 = sub: i32  0, 1
	%39 = mul: i32  %37, %38
	%40: i32 = d_11
	%41: i32 = i_11
	%42 = add: i32  %41, 1
	%43 = div: i32  %40, %42
	%44 = mod: i32  %43, 2
	%45 = add: i32  %39, %44
	%46 = add: i32  %45, 0
	c_11: i32 = %46
	%47: i32 = c_11
	param: i32  %47
	call: void  putint
	param: i8  10
	call: void  putchar
	%48: i32 = c_11
	%49 = gre: i32  %48, 0
	if false %49 goto  $L4
	param: i8  112
	call: void  putchar
	param: i8  97
	call: void  putchar
	param: i8  115
	call: void  putchar
	param: i8  115
	call: void  putchar
	param: i8  10
	call: void  putchar
	goto  $L5

$L4:
	param: i8  102
	call: void  putchar
	param: i8  97
	call: void  putchar
	param: i8  105
	call: void  putchar
	param: i8  108
	call: void  putchar
	param: i8  101
	call: void  putchar
	param: i8  100
	call: void  putchar
	param: i8  10
	call: void  putchar

$L5:
	%50: i32 = i_11
	%51 = geq: i32  %50, 0
	if false %51 goto  $L3
	%52: i32 = i_11
	%53 = add: i32  %52, 1
	i_11: i32 = %53
	goto  $L6

$L3:
	i_11: i32 = 0
	%54: i32 = i_11
	%55: i32 = d_11
	%56 = lss: i32  %54, %55
	if false %56 goto  $L7

$L7:
	i_11: i32 = 0
	i_11: i32 = 0
	%61: i32 = i_11
	%62: i32 = d_11
	%63 = lss: i32  %61, %62
	if false %63 goto  $L8

$L8:
	%66: i32 = i_11
	%67: i32 = d_11
	%68 = lss: i32  %66, %67
	if false %68 goto  $L9

$L9:
	i_11: i32 = 0
	if false 1 goto  $L10

$L10:
	%69: i32 = d_11
	%70: i32 = a_11
	%71 = geq: i32  %69, %70
	if false %71 goto  $L11

$L11:
	%72: i32 = d_11
	%73: i32 = a_11
	%74 = leq: i32  %72, %73
	if false %74 goto  $L12

$L12:
	%75: i32 = d_11
	%76: i32 = a_11
	%77 = eql: i32  %75, %76
	if false %77 goto  $L13

$L13:
	%78: i32 = d_11
	%79: i32 = a_11
	%80 = gre: i32  %78, %79
	if false %80 goto  $L14

$L14:
	%81: i32 = d_11
	%82: i32 = a_11
	%83 = lss: i32  %81, %82
	if false %83 goto  $L15

$L15:
	%84: i32 = d_11
	%85: i32 = a_11
	%86 = neq: i32  %84, %85
	if false %86 goto  $L16

$L16:
	%87 = call: i32  f6
	if false 1 goto  $L17
	param: i32  1
	call: void  f2

$L17:
	%88: i32 = 0
	if false 0 goto  $L18
	%89: i32 = d_11
	%90: i32 = a_11
	%91 = neq: i32  %89, %90
	if false %91 goto  $L18
	%88: i32 = 1

$L18:
	if false %88 goto  $L19

$L19:
	%92: i32 = 1
	if 1 goto  $L20
	%93: i32 = a_11
	%94 = gre: i32  %93, 0
	if %94 goto  $L20
	%92: i32 = 0

$L20:
	if false %92 goto  $L21

$L21:
	%95: i32 = 1
	%96: i32 = d_11
	%97 = eql: i32  %96, 0
	%98: i32 = a_11
	%99 = eql: i32  %97, %98
	if %99 goto  $L22
	%100: i32 = 0
	%101: i32 = a_11
	%102 = gre: i32  %101, 0
	if false %102 goto  $L23
	%103 = eql: i32  1, 1
	if false %103 goto  $L23
	%104: i32 = d_11
	%105 = add: i32  %104, 1
	%106 = mod: i32  %105, 2
	%107 = div: i32  %106, 2
	if false %107 goto  $L23
	%100: i32 = 1

$L23:
	if %100 goto  $L22
	%95: i32 = 0

$L22:
	if false %95 goto  $L24

$L24:
	ret: i32  0