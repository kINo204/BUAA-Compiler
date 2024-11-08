global e_c_1[20]: i8 = 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0

define fun oerr:
	i_2 = alloc: i32
	i_2: i32 = 0

$L0_for_cond:
	%1: i32 = i_2
	%2 = lss: i32  %1, 20
	if false %2 goto  $L2_for_end
	%4: i32 = i_2
	%3: i8 = e_c_1[%4]
	if false %3 goto  $L3_if_end
	%6: i32 = i_2
	%5: i8 = e_c_1[%6]
	param  %5
	call: void  putchar

$L3_if_end:

$L1_for_motion:
	%7: i32 = i_2
	%8 = add: i32  %7, 1
	i_2: i32 = %8
	goto  $L0_for_cond

$L2_for_end:
	ret

define fun side_effect:
	param  115
	call: void  putchar
	param  105
	call: void  putchar
	param  100
	call: void  putchar
	param  101
	call: void  putchar
	param  32
	call: void  putchar
	param  101
	call: void  putchar
	param  102
	call: void  putchar
	param  102
	call: void  putchar
	param  101
	call: void  putchar
	param  99
	call: void  putchar
	param  116
	call: void  putchar
	param  32
	call: void  putchar
	%1: i32 = num_4
	param  %1
	call: void  putint
	param  10
	call: void  putchar
	ret: i32  1

define fun main:
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
	%1: i32 = 0
	if false 1 goto  $L5_landexp_end
	if false 0 goto  $L5_landexp_end
	%1: i32 = 1
	goto  $L5_landexp_end

$L5_landexp_end:
	if false %1 goto  $L4_if_end
	e_c_1[0]: i8 = 49

$L4_if_end:
	%2: i32 = 1
	%3: i32 = 0
	if false 1 goto  $L8_landexp_end
	if false 0 goto  $L8_landexp_end
	%3: i32 = 1
	goto  $L8_landexp_end

$L8_landexp_end:
	if %3 goto  $L7_lorexp_end
	%4: i32 = 0
	if false 0 goto  $L9_landexp_end
	if false 4 goto  $L9_landexp_end
	%4: i32 = 1
	goto  $L9_landexp_end

$L9_landexp_end:
	if %4 goto  $L7_lorexp_end
	%2: i32 = 0
	goto  $L7_lorexp_end

$L7_lorexp_end:
	if false %2 goto  $L6_if_end
	e_c_1[0]: i8 = 49

$L6_if_end:
	param  70
	call: void  putchar
	param  76
	call: void  putchar
	param  65
	call: void  putchar
	param  71
	call: void  putchar
	param  49
	call: void  putchar
	param  10
	call: void  putchar
	%5: i32 = 0
	if false 0 goto  $L11_landexp_end
	param  0
	%6 = call: i32  side_effect
	if false %6 goto  $L11_landexp_end
	%5: i32 = 1
	goto  $L11_landexp_end

$L11_landexp_end:
	if false %5 goto  $L10_if_end

$L10_if_end:
	param  70
	call: void  putchar
	param  76
	call: void  putchar
	param  65
	call: void  putchar
	param  71
	call: void  putchar
	param  50
	call: void  putchar
	param  10
	call: void  putchar
	%7: i32 = 0
	if false 1 goto  $L13_landexp_end
	param  1
	%8 = call: i32  side_effect
	if false %8 goto  $L13_landexp_end
	%7: i32 = 1
	goto  $L13_landexp_end

$L13_landexp_end:
	if false %7 goto  $L12_if_end

$L12_if_end:
	param  70
	call: void  putchar
	param  76
	call: void  putchar
	param  65
	call: void  putchar
	param  71
	call: void  putchar
	param  51
	call: void  putchar
	param  10
	call: void  putchar
	%9: i32 = 1
	if 0 goto  $L15_lorexp_end
	param  2
	%10 = call: i32  side_effect
	if %10 goto  $L15_lorexp_end
	%9: i32 = 0
	goto  $L15_lorexp_end

$L15_lorexp_end:
	if false %9 goto  $L14_if_end

$L14_if_end:
	param  70
	call: void  putchar
	param  76
	call: void  putchar
	param  65
	call: void  putchar
	param  71
	call: void  putchar
	param  52
	call: void  putchar
	param  10
	call: void  putchar
	%11: i32 = 1
	if 1 goto  $L17_lorexp_end
	param  3
	%12 = call: i32  side_effect
	if %12 goto  $L17_lorexp_end
	%11: i32 = 0
	goto  $L17_lorexp_end

$L17_lorexp_end:
	if false %11 goto  $L16_if_end

$L16_if_end:
	param  70
	call: void  putchar
	param  76
	call: void  putchar
	param  65
	call: void  putchar
	param  71
	call: void  putchar
	param  53
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
	call: void  oerr
	ret: i32  0