global a1_1[10]: i32 = 0, 0, 0, 0, 0, 0, 0, 0, 0, 0

global a2_1[5]: i32 = 0, 0, 0, 0, 0

global a3_1[5]: i32 = 0, 0, 0, 0, 0

global a4_1[5]: i32 = 1, 2, 3, 0, 0

global c1_1[10]: i8 = 0, 0, 0, 0, 0, 0, 0, 0, 0, 0

global c2_1[5]: i8 = 0, 0, 0, 0, 0

global c3_1[5]: i8 = 0, 0, 0, 0, 0

global c4_1[5]: i8 = 104, 101, 121, 0, 0

global c5_1[10]: i8 = 104, 101, 108, 108, 111, 0, 0, 0, 0, 0

define fun f1:
	sum_2 = alloc: i32
	sum_2: i32 = 0
	i_2 = alloc: i32
	i_2: i32 = 0

$L0_for_cond:
	%1: i32 = i_2 < len_2
	if false %1 goto  $L2_for_end
	%2: i32 = *(a_2)[i_2]
	%3: i32 = sum_2 + %2
	sum_2: i32 = %3

$L1_for_motion:
	%4: i32 = i_2 + 1
	i_2: i32 = %4
	goto  $L0_for_cond

$L2_for_end:
	ret: i32  sum_2

define fun f2:
	%1: i32 = n_4 == 1
	if false %1 goto  $L3_if_else
	ret: i32  1
	goto  $L4_if_end

$L3_if_else:
	%2: i32 = n_4 == 2
	if false %2 goto  $L5_if_else
	ret: i32  1
	goto  $L6_if_end

$L5_if_else:
	%3: i32 = n_4 - 1
	param: i32  %3
	%4 = call: i32  f2
	%5: i32 = n_4 - 2
	param: i32  %5
	%6 = call: i32  f2
	%7: i32 = %4 + %6
	ret: i32  %7

$L6_if_end:

$L4_if_end:
	ret: i32  -1

define fun f3:
	%1: i8 = *(s_5)[0]
	param: i8  %1
	call: void  putchar
	param: i8  10
	call: void  putchar
	ret

define fun f4:
	i_6 = alloc: i32
	sum_6 = alloc: i32
	sum_6: i32 = 0
	i_6: i32 = 0

$L7_for_cond:
	%1: i32 = i_6 < len_6
	if false %1 goto  $L9_for_end
	%2: i32 = *(a_6)[i_6]
	%3: i32 = *(b_6)[i_6]
	%4: i32 = %2 * %3
	%5: i32 = sum_6 + %4
	sum_6: i32 = %5

$L8_for_motion:
	%6: i32 = i_6 + 1
	i_6: i32 = %6
	goto  $L7_for_cond

$L9_for_end:
	ret: i32  sum_6

define fun main:
	n_8 = alloc: i32
	param: i8  50
	call: void  putchar
	param: i8  49
	call: void  putchar
	param: i8  51
	call: void  putchar
	param: i8  55
	call: void  putchar
	param: i8  52
	call: void  putchar
	param: i8  50
	call: void  putchar
	param: i8  55
	call: void  putchar
	param: i8  53
	call: void  putchar
	param: i8  10
	call: void  putchar
	a4_1[3]: i32 = 4
	a4_1[4]: i32 = 5
	sum_8 = alloc: i32
	%1: &i32 = &(a4_1)
	param: i32  %1
	param: i32  5
	%2 = call: i32  f1
	sum_8: i32 = %2
	param: i8  115
	call: void  putchar
	param: i8  117
	call: void  putchar
	param: i8  109
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i8  61
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i32  sum_8
	call: void  putint
	param: i8  10
	call: void  putchar
	%3: i8 = c4_1[0]
	param: i8  99
	call: void  putchar
	param: i8  52
	call: void  putchar
	param: i8  91
	call: void  putchar
	param: i8  48
	call: void  putchar
	param: i8  93
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i8  61
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i8  %3
	call: void  putchar
	param: i8  10
	call: void  putchar
	%4: i8 = c5_1[0]
	param: i8  99
	call: void  putchar
	param: i8  53
	call: void  putchar
	param: i8  91
	call: void  putchar
	param: i8  48
	call: void  putchar
	param: i8  93
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i8  61
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i8  %4
	call: void  putchar
	param: i8  10
	call: void  putchar
	t_8 = alloc: i32
	t_8: i32 = 100
	%5: i32 = t_8 + 1
	%6: i32 = %5 % 25
	%7: i32 = %6 * 5
	%8: i32 = 3 / 2
	%9: i32 = %7 - %8
	t_8: i32 = %9
	param: i8  116
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i8  61
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i32  t_8
	call: void  putint
	param: i8  10
	call: void  putchar
	param: i32  10
	%10 = call: i32  f2
	t_8: i32 = %10
	param: i32  t_8
	call: void  putint
	param: i8  10
	call: void  putchar
	s_8 = alloc: i8  10
	s_8[0]: i8 = 49
	s_8[1]: i8 = 50
	s_8[2]: i8 = 51
	s_8[3]: i8 = 52
	s_8[4]: i8 = 53
	s_8[5]: i8 = 54
	s_8[6]: i8 = 0
	s_8[7]: i8 = 0
	s_8[8]: i8 = 0
	s_8[9]: i8 = 0
	%11: &i32 = &(s_8)
	param: i32  %11
	call: void  f3
	%12: &i32 = &(c4_1)
	param: i32  %12
	call: void  f3
	%13: &i32 = &(c5_1)
	param: i32  %13
	call: void  f3
	a5_8 = alloc: i32  3
	a5_8[0]: i32 = 2
	a5_8[1]: i32 = 3
	a5_8[2]: i32 = 4
	a6_8 = alloc: i32  3
	a6_8[0]: i32 = 1
	a6_8[1]: i32 = 5
	a6_8[2]: i32 = 7
	%14: &i32 = &(a5_8)
	%15: &i32 = &(a6_8)
	%16: i32 = a5_8[1]
	param: i32  %14
	param: i32  %15
	param: i32  %16
	%17 = call: i32  f4
	%18: i32 = %17 + 1
	param: i8  115
	call: void  putchar
	param: i8  117
	call: void  putchar
	param: i8  109
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i8  43
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i8  49
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i8  61
	call: void  putchar
	param: i8  32
	call: void  putchar
	param: i32  %18
	call: void  putint
	param: i8  10
	call: void  putchar
	ret: i32  0