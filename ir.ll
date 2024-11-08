global ci_1: i32 = 1

global ciexp_1: i32 = 0

global ca_1: i32 = 1

global cb_1: i32 = 2

global i_1: i32 = 1

global iexp_1: i32 = 0

global a_1: i32 = 1

global b_1: i32 = 2

define fun funv:
	param  102
	call: void  putchar
	param  117
	call: void  putchar
	param  110
	call: void  putchar
	param  118
	call: void  putchar
	param  10
	call: void  putchar
	ret

define fun funi:
	param  102
	call: void  putchar
	param  117
	call: void  putchar
	param  110
	call: void  putchar
	param  105
	call: void  putchar
	param  10
	call: void  putchar
	ret: i32  0

define fun funvnop:
	ret: i32  0

define fun main:
	ma1_6 = alloc: i32
	ma2_6 = alloc: i32
	ma3_6 = alloc: i32
	ma4_6 = alloc: i32
	ma1_6: i32 = 1
	ma2_6: i32 = 2
	ma3_6: i32 = 1
	ma4_6: i32 = 1
	%1: i32 = ma1_6
	%2: i32 = ma2_6
	%3 = neq: i32  %1, %2
	if false %3 goto  $L0_if_else
	mif1_7 = alloc: i32
	mif1_7: i32 = 1
	%4: i32 = mif1_7
	param  %4
	call: void  putint
	param  10
	call: void  putchar
	goto  $L1_if_end

$L0_if_else:
	ret: i32  0

$L1_if_end:
	%5: i32 = ma1_6
	%6: i32 = ma3_6
	%7 = eql: i32  %5, %6
	if false %7 goto  $L2_if_end
	%8: i32 = ma1_6
	param  %8
	%9 = call: i32  funi

$L2_if_end:
	%10: i32 = ma1_6
	%11: i32 = ma3_6
	%12 = neq: i32  %10, %11
	if false %12 goto  $L3_if_end
	%13 = call: i32  funvnop

$L3_if_end:
	%14: i32 = ma1_6
	%15: i32 = ma3_6
	%16 = geq: i32  %14, %15
	if false %16 goto  $L4_if_end

$L4_if_end:
	%17: i32 = ma2_6
	%18: i32 = ma3_6
	%19 = leq: i32  %17, %18
	if false %19 goto  $L5_if_end
	%20 = call: i32  funvnop

$L5_if_end:
	%21: i32 = ma1_6
	%22: i32 = ma3_6
	%23 = gre: i32  %21, %22
	if false %23 goto  $L6_if_end
	%24 = call: i32  funvnop

$L6_if_end:
	%25: i32 = ma2_6
	%26: i32 = ma3_6
	%27 = lss: i32  %25, %26
	if false %27 goto  $L7_if_end
	%28 = call: i32  funvnop

$L7_if_end:
	%29 = call: i32  funvnop

$L8_for_cond:
stack rem
	%30: i32 = ma1_6
	%31: i32 = ma4_6
	%32 = eql: i32  %30, %31
	if false %32 goto  $L10_for_end
	ma4_6: i32 = 2
	goto  $L9_for_motion

$L9_for_motion:
stack lod
	goto  $L8_for_cond

$L10_for_end:
stack lod

$L11_for_cond:
stack rem
	%33: i32 = ma1_6
	%34: i32 = ma4_6
	%35 = eql: i32  %33, %34
	if false %35 goto  $L13_for_end
	goto  $L13_for_end

$L12_for_motion:
stack lod
	goto  $L11_for_cond

$L13_for_end:
stack lod
	param  116
	call: void  putchar
	param  114
	call: void  putchar
	param  121
	call: void  putchar
	param  10
	call: void  putchar
	param  116
	call: void  putchar
	param  114
	call: void  putchar
	param  121
	call: void  putchar
	param  10
	call: void  putchar
	param  116
	call: void  putchar
	param  114
	call: void  putchar
	param  121
	call: void  putchar
	param  10
	call: void  putchar
	param  116
	call: void  putchar
	param  114
	call: void  putchar
	param  121
	call: void  putchar
	param  10
	call: void  putchar
	param  116
	call: void  putchar
	param  114
	call: void  putchar
	param  121
	call: void  putchar
	param  10
	call: void  putchar
	param  116
	call: void  putchar
	param  114
	call: void  putchar
	param  121
	call: void  putchar
	param  10
	call: void  putchar
	param  116
	call: void  putchar
	param  114
	call: void  putchar
	param  121
	call: void  putchar
	param  10
	call: void  putchar
	ret: i32  0