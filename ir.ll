global globalInt_1: i32 = 10

global globalChar_1: i8 = 97

global globalIntArray_1[100]: i32 = 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0

global globalCharArray_1[100]: i8 = 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0

define fun printInt:
	param: i32  value_2
	call: void  putint
	param: i8  10
	call: void  putchar
	ret

define fun printChar:
	param: i8  value_3
	call: void  putchar
	param: i8  10
	call: void  putchar
	ret

define fun updateArray:
	i_4 = alloc: i32
	i_4: i32 = 0

$L0_for_cond:
	%1: i32 = i_4 < size_4
	if false %1 goto  $L2_for_end
	*(arr_4)[i_4]: i32 = value_4

$L1_for_motion:
	%2: i32 = i_4 + 1
	i_4: i32 = %2
	goto  $L0_for_cond

$L2_for_end:
	ret

define fun processElements:
	i_6 = alloc: i32
	i_6: i32 = 0

$L3_for_cond:
	%1: i32 = i_6 < size_6
	if false %1 goto  $L5_for_end
	%2: i32 = *(arr_6)[i_6]
	%3: i32 = %2 * multiplier_6
	*(arr_6)[i_6]: i32 = %3
	%4: i32 = *(arr_6)[i_6]
	%5: i32 = %4 + addChar_6
	*(carr_6)[i_6]: i8 = %5

$L4_for_motion:
	%6: i32 = i_6 + 1
	i_6: i32 = %6
	goto  $L3_for_cond

$L5_for_end:
	ret

define fun compareAndSum:
	sum_8 = alloc: i32
	sum_8: i32 = 0
	%1: i32 = 0
	%2: i32 = a_8 > b_8
	if false %2 goto  $L8_landexp_end
	%3: i32 = c_8 < d_8
	if false %3 goto  $L8_landexp_end
	%1: i32 = 1

$L8_landexp_end:
	if false %1 goto  $L6_if_else
	%4: i32 = a_8 + c_8
	sum_8: i32 = %4
	goto  $L7_if_end

$L6_if_else:
	%5: i32 = b_8 + d_8
	sum_8: i32 = %5

$L7_if_end:
	ret: i32  sum_8

define fun complexOperation:
	i_11 = alloc: i32
	param: i32  arr_11
	param: i32  size_11
	param: i32  multiplier_11
	call: void  updatearray
	param: i32  arr_11
	param: i32  carr_11
	param: i32  size_11
	param: i32  multiplier_11
	param: i8  addchar_11
	call: void  processelements
	i_11: i32 = 0

$L9_for_cond:
	%1: i32 = i_11 < size_11
	if false %1 goto  $L11_for_end
	%2: i32 = *(arr_11)[i_11]
	%3: i32 = %2 > threshold_11
	if false %3 goto  $L12_if_else
	%4: i32 = *(arr_11)[i_11]
	param: i32  %4
	call: void  printint
	goto  $L13_if_end

$L12_if_else:
	%5: i8 = *(carr_11)[i_11]
	param: i8  %5
	call: void  printchar

$L13_if_end:

$L10_for_motion:
	%6: i32 = i_11 + 1
	i_11: i32 = %6
	goto  $L9_for_cond

$L11_for_end:
	ret

define fun main:
	mainint_15 = alloc: i32
	mainInt_15: i32 = 5
	mainchar_15 = alloc: i8
	mainChar_15: i8 = 122
	localint_15 = alloc: i32
	localInt_15: i32 = 3
	localchar_15 = alloc: i8
	localChar_15: i8 = 121
	size_15 = alloc: i32
	size_15: i32 = 10
	%1: &i32 = &(globalIntArray_1)
	param: i32  %1
	param: i32  size_15
	param: i32  globalint_1
	call: void  updatearray
	i_15 = alloc: i32
	i_15: i32 = 0

$L14_for_cond:
	%2: i32 = i_15 < size_15
	if false %2 goto  $L16_for_end
	globalCharArray_1[i_15]: i8 = globalChar_1

$L15_for_motion:
	%3: i32 = i_15 + 1
	i_15: i32 = %3
	goto  $L14_for_cond

$L16_for_end:
	%4: &i32 = &(globalIntArray_1)
	%5: &i32 = &(globalCharArray_1)
	param: i32  %4
	param: i32  %5
	param: i32  size_15
	param: i32  localint_15
	param: i8  localchar_15
	param: i32  20
	call: void  complexoperation
	result_15 = alloc: i32
	param: i32  globalint_1
	param: i32  mainint_15
	param: i32  localint_15
	param: i32  8
	%6 = call: i32  compareandsum
	result_15: i32 = %6
	param: i32  result_15
	call: void  printint
	ret: i32  0