define fun de:
	ret

define fun keke:
	%1: i32 = i_3
	%2: i32 = j_3
	%3 = add: i32  %1, %2
	i_3: i32 = %3
	ret: i32  0

define fun jian:
	x_4 = alloc: i32
	y_4 = alloc: i32
	z_4 = alloc: i32
	%1 = call: i32  getint
	x_4: i32 = %1
	%2 = call: i32  getint
	y_4: i32 = %2
	%3: i32 = x_4
	%4: i32 = y_4
	%5 = sub: i32  %3, %4
	z_4: i32 = %5
	%6: i32 = z_4
	ret: i32  %6

define fun main:
	a_5 = alloc: i32
	b_5 = alloc: i32
	c_5 = alloc: i32
	d_5 = alloc: i32
	e_5 = alloc: i32
	f_5 = alloc: i32
	g_5 = alloc: i32
	g_5: i32 = 1
	h_5 = alloc: i32
	j_5 = alloc: i32
	k_5 = alloc: i32
	l_5 = alloc: i32
	o_5 = alloc: i32
	o_5: i32 = -1
	i_5 = alloc: i32
	i_5: i32 = 2
	n_5 = alloc: i32
	m_5 = alloc: i32
	flag_5 = alloc: i32
	flag_5: i32 = 0
	%1 = call: i32  getint
	n_5: i32 = %1

$L0_for_cond:
	%2: i32 = i_5
	%3: i32 = n_5
	%4 = lss: i32  %2, %3
	if false %4 goto  $L2_for_end
	%5: i32 = n_5
	%6: i32 = i_5
	%7 = mod: i32  %5, %6
	m_5: i32 = %7
	%8: i32 = m_5
	%9 = eql: i32  %8, 0
	if false %9 goto  $L3_if_end
	flag_5: i32 = 1
	param: i8  48
	call: void  putchar
	param: i8  10
	call: void  putchar

$L3_if_end:
	%10: i32 = i_5
	%11 = add: i32  %10, 1
	i_5: i32 = %11

$L1_for_motion:
	goto  $L0_for_cond

$L2_for_end:
	%12 = call: i32  jian
	c_5: i32 = %12
	%13: i32 = c_5
	param: i32  %13
	call: void  putint
	param: i8  10
	call: void  putchar
	%14: i32 = c_5
	%15 = add: i32  %14, 1
	d_5: i32 = %15
	%16: i32 = c_5
	%17 = mul: i32  %16, 2
	e_5: i32 = %17
	%18: i32 = e_5
	%19 = lss: i32  %18, 5
	if false %19 goto  $L4_if_else
	%20: i32 = c_5
	%21 = mod: i32  %20, 2
	f_5: i32 = %21
	goto  $L5_if_end

$L4_if_else:
	%22: i32 = c_5
	%23 = div: i32  %22, 2
	f_5: i32 = %23

$L5_if_end:
	%24: i32 = f_5
	%25 = neq: i32  %24, 0
	if false %25 goto  $L6_if_end
	%26: i32 = g_5
	%27 = add: i32  %26, 1
	g_5: i32 = %27

$L6_if_end:
	%28: i32 = i_5
	%29: i32 = j_5
	%30 = add: i32  %29, 1
	%31 = add: i32  %28, %30
	o_5: i32 = %31

$L7_for_cond:
	if false 0 goto  $L9_for_end
	goto  $L8_for_motion

$L8_for_motion:
	goto  $L7_for_cond

$L9_for_end:

$L10_for_cond:
	if false 1 goto  $L12_for_end
	goto  $L12_for_end

$L11_for_motion:
	goto  $L10_for_cond

$L12_for_end:
	%32: i32 = c_5
	%33: i32 = d_5
	%34 = eql: i32  %32, %33
	if false %34 goto  $L13_if_end
	%35: i32 = d_5
	%36: i32 = e_5
	%37 = geq: i32  %35, %36
	if false %37 goto  $L14_if_end
	%38: i32 = e_5
	%39: i32 = f_5
	%40 = leq: i32  %38, %39
	if false %40 goto  $L15_if_end
	%41: i32 = f_5
	%42: i32 = g_5
	%43 = neq: i32  %41, %42
	if false %43 goto  $L16_if_end
	%44: i32 = c_5
	%45 = gre: i32  %44, 1
	if false %45 goto  $L17_if_end
	a_5: i32 = 1

$L17_if_end:

$L16_if_end:

$L15_if_end:

$L14_if_end:

$L13_if_end:
	%46: i32 = a_5
	%47: i32 = b_5
	param: i32  %46
	param: i32  %47
	%48 = call: i32  keke
	%49: i32 = d_5
	%50: i32 = e_5
	%51: i32 = f_5
	%52: i32 = g_5
	param: i32  %49
	call: void  putint
	param: i8  10
	call: void  putchar
	param: i32  %50
	call: void  putint
	param: i8  10
	call: void  putchar
	param: i32  %51
	call: void  putint
	param: i8  10
	call: void  putchar
	param: i32  %52
	call: void  putint
	param: i8  10
	call: void  putchar
	param: i8  49
	call: void  putchar
	param: i8  57
	call: void  putchar
	param: i8  49
	call: void  putchar
	param: i8  56
	call: void  putchar
	param: i8  50
	call: void  putchar
	param: i8  54
	call: void  putchar
	param: i8  50
	call: void  putchar
	param: i8  48
	call: void  putchar
	param: i8  10
	call: void  putchar
	param: i8  49
	call: void  putchar
	param: i8  57
	call: void  putchar
	param: i8  49
	call: void  putchar
	param: i8  56
	call: void  putchar
	param: i8  50
	call: void  putchar
	param: i8  54
	call: void  putchar
	param: i8  50
	call: void  putchar
	param: i8  48
	call: void  putchar
	param: i8  10
	call: void  putchar
	param: i8  49
	call: void  putchar
	param: i8  57
	call: void  putchar
	param: i8  49
	call: void  putchar
	param: i8  56
	call: void  putchar
	param: i8  50
	call: void  putchar
	param: i8  54
	call: void  putchar
	param: i8  50
	call: void  putchar
	param: i8  48
	call: void  putchar
	param: i8  10
	call: void  putchar
	ret: i32  0