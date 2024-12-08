digraph printInt {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="param: i32  value_2\lcall: void  putint\lparam: i8  10\lcall: void  putchar\lret\l"]
	B0 -> Exit

}

digraph printChar {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="param: i8  value_3\lcall: void  putchar\lparam: i8  10\lcall: void  putchar\lret\l"]
	B0 -> Exit

}

digraph updateArray {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="i_4 = alloc: i32\li_4: i32 = 0\l"]
	B0 -> B1

    B1 [shape=box xlabel="B1" label="%1: i32 = i_4 < size_4\l"]
	B1 -> B2 [label="%1 T"]
	B1 -> B3 [label="%1 F"]

    B2 [shape=box xlabel="B2" label="*(arr_4)[i_4]: i32 = value_4\l%2: i32 = i_4 + 1\li_4: i32 = %2\l"]
	B2 -> B1

    B3 [shape=box xlabel="B3" label="ret\l"]
	B3 -> Exit

}

digraph processElements {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="i_6 = alloc: i32\li_6: i32 = 0\l"]
	B0 -> B1

    B1 [shape=box xlabel="B1" label="%1: i32 = i_6 < size_6\l"]
	B1 -> B2 [label="%1 T"]
	B1 -> B3 [label="%1 F"]

    B2 [shape=box xlabel="B2" label="%2: i32 = *(arr_6)[i_6]\l%3: i32 = %2 * multiplier_6\l*(arr_6)[i_6]: i32 = %3\l%4: i32 = *(arr_6)[i_6]\l%5: i32 = %4 + addChar_6\l*(carr_6)[i_6]: i8 = %5\l%6: i32 = i_6 + 1\li_6: i32 = %6\l"]
	B2 -> B1

    B3 [shape=box xlabel="B3" label="ret\l"]
	B3 -> Exit

}

digraph compareAndSum {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="sum_8 = alloc: i32\lsum_8: i32 = 0\l%1: i32 = 0\l%2: i32 = a_8 > b_8\l"]
	B0 -> B1 [label="%2 T"]
	B0 -> B3 [label="%2 F"]

    B1 [shape=box xlabel="B1" label="%3: i32 = c_8 < d_8\l"]
	B1 -> B2 [label="%3 T"]
	B1 -> B3 [label="%3 F"]

    B2 [shape=box xlabel="B2" label="%1: i32 = 1\l"]
	B2 -> B3

    B3 [shape=box xlabel="B3" label=""]
	B3 -> B4 [label="%1 T"]
	B3 -> B5 [label="%1 F"]

    B4 [shape=box xlabel="B4" label="%4: i32 = a_8 + c_8\lsum_8: i32 = %4\l"]
	B4 -> B6

    B5 [shape=box xlabel="B5" label="%5: i32 = b_8 + d_8\lsum_8: i32 = %5\l"]
	B5 -> B6

    B6 [shape=box xlabel="B6" label="ret: i32  sum_8\l"]
	B6 -> Exit

}

digraph complexOperation {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="i_11 = alloc: i32\lparam: i32  arr_11\lparam: i32  size_11\lparam: i32  multiplier_11\lcall: void  updatearray\lparam: i32  arr_11\lparam: i32  carr_11\lparam: i32  size_11\lparam: i32  multiplier_11\lparam: i8  addchar_11\lcall: void  processelements\li_11: i32 = 0\l"]
	B0 -> B1

    B1 [shape=box xlabel="B1" label="%1: i32 = i_11 < size_11\l"]
	B1 -> B2 [label="%1 T"]
	B1 -> B6 [label="%1 F"]

    B2 [shape=box xlabel="B2" label="%2: i32 = *(arr_11)[i_11]\l%3: i32 = %2 > threshold_11\l"]
	B2 -> B3 [label="%3 T"]
	B2 -> B4 [label="%3 F"]

    B3 [shape=box xlabel="B3" label="%4: i32 = *(arr_11)[i_11]\lparam: i32  %4\lcall: void  printint\l"]
	B3 -> B5

    B4 [shape=box xlabel="B4" label="%5: i8 = *(carr_11)[i_11]\lparam: i8  %5\lcall: void  printchar\l"]
	B4 -> B5

    B5 [shape=box xlabel="B5" label="%6: i32 = i_11 + 1\li_11: i32 = %6\l"]
	B5 -> B1

    B6 [shape=box xlabel="B6" label="ret\l"]
	B6 -> Exit

}

digraph main {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="mainint_15 = alloc: i32\lmainInt_15: i32 = 5\lmainchar_15 = alloc: i8\lmainChar_15: i8 = 122\llocalint_15 = alloc: i32\llocalInt_15: i32 = 3\llocalchar_15 = alloc: i8\llocalChar_15: i8 = 121\lsize_15 = alloc: i32\lsize_15: i32 = 10\l%1: &i32 = &(globalIntArray_1)\lparam: i32  %1\lparam: i32  size_15\lparam: i32  globalint_1\lcall: void  updatearray\li_15 = alloc: i32\li_15: i32 = 0\l"]
	B0 -> B1

    B1 [shape=box xlabel="B1" label="%2: i32 = i_15 < size_15\l"]
	B1 -> B2 [label="%2 T"]
	B1 -> B3 [label="%2 F"]

    B2 [shape=box xlabel="B2" label="globalCharArray_1[i_15]: i8 = globalChar_1\l%3: i32 = i_15 + 1\li_15: i32 = %3\l"]
	B2 -> B1

    B3 [shape=box xlabel="B3" label="%4: &i32 = &(globalIntArray_1)\l%5: &i32 = &(globalCharArray_1)\lparam: i32  %4\lparam: i32  %5\lparam: i32  size_15\lparam: i32  localint_15\lparam: i8  localchar_15\lparam: i32  20\lcall: void  complexoperation\lresult_15 = alloc: i32\lparam: i32  globalint_1\lparam: i32  mainint_15\lparam: i32  localint_15\lparam: i32  8\l%6 = call: i32  compareandsum\lresult_15: i32 = %6\lparam: i32  result_15\lcall: void  printint\lret: i32  0\l"]
	B3 -> Exit

}

