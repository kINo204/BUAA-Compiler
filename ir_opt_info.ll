digraph f1 {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="sum_2 = alloc: i32\lsum_2: i32 = 0\li_2 = alloc: i32\li_2: i32 = 0\l"]
	B0 -> B1

    B1 [shape=box xlabel="B1" label="%1: i32 = i_2 < len_2\l"]
	B1 -> B2 [label="%1 T"]
	B1 -> B3 [label="%1 F"]

    B2 [shape=box xlabel="B2" label="%2: i32 = *(a_2)[i_2]\l%3: i32 = sum_2 + %2\lsum_2: i32 = %3\l%4: i32 = i_2 + 1\li_2: i32 = %4\l"]
	B2 -> B1

    B3 [shape=box xlabel="B3" label="ret: i32  sum_2\l"]
	B3 -> Exit

}

digraph f2 {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="%1: i32 = n_4 == 1\l"]
	B0 -> B1 [label="%1 T"]
	B0 -> B2 [label="%1 F"]

    B1 [shape=box xlabel="B1" label="ret: i32  1\l"]
	B1 -> Exit

    B2 [shape=box xlabel="B2" label="%2: i32 = n_4 == 2\l"]
	B2 -> B3 [label="%2 T"]
	B2 -> B4 [label="%2 F"]

    B3 [shape=box xlabel="B3" label="ret: i32  1\l"]
	B3 -> Exit

    B4 [shape=box xlabel="B4" label="%3: i32 = n_4 - 1\lparam: i32  %3\l%4 = call: i32  f2\l%5: i32 = n_4 - 2\lparam: i32  %5\l%6 = call: i32  f2\l%7: i32 = %4 + %6\lret: i32  %7\l"]
	B4 -> Exit

}

digraph f3 {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="%1: i8 = *(s_5)[0]\lparam: i8  %1\lcall: void  putchar\lparam: i8  10\lcall: void  putchar\lret\l"]
	B0 -> Exit

}

digraph f4 {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="i_6 = alloc: i32\lsum_6 = alloc: i32\lsum_6: i32 = 0\li_6: i32 = 0\l"]
	B0 -> B1

    B1 [shape=box xlabel="B1" label="%1: i32 = i_6 < len_6\l"]
	B1 -> B2 [label="%1 T"]
	B1 -> B3 [label="%1 F"]

    B2 [shape=box xlabel="B2" label="%2: i32 = *(a_6)[i_6]\l%3: i32 = *(b_6)[i_6]\l%4: i32 = %2 * %3\l%5: i32 = sum_6 + %4\lsum_6: i32 = %5\l%6: i32 = i_6 + 1\li_6: i32 = %6\l"]
	B2 -> B1

    B3 [shape=box xlabel="B3" label="ret: i32  sum_6\l"]
	B3 -> Exit

}

digraph main {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="n_8 = alloc: i32\lparam: i8  50\lcall: void  putchar\lparam: i8  49\lcall: void  putchar\lparam: i8  51\lcall: void  putchar\lparam: i8  55\lcall: void  putchar\lparam: i8  52\lcall: void  putchar\lparam: i8  50\lcall: void  putchar\lparam: i8  55\lcall: void  putchar\lparam: i8  53\lcall: void  putchar\lparam: i8  10\lcall: void  putchar\la4_1[3]: i32 = 4\la4_1[4]: i32 = 5\lsum_8 = alloc: i32\l%1: &i32 = &(a4_1)\lparam: i32  %1\lparam: i32  5\l%2 = call: i32  f1\lsum_8: i32 = %2\lparam: i8  115\lcall: void  putchar\lparam: i8  117\lcall: void  putchar\lparam: i8  109\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\lparam: i8  61\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\lparam: i32  sum_8\lcall: void  putint\lparam: i8  10\lcall: void  putchar\l%3: i8 = c4_1[0]\lparam: i8  99\lcall: void  putchar\lparam: i8  52\lcall: void  putchar\lparam: i8  91\lcall: void  putchar\lparam: i8  48\lcall: void  putchar\lparam: i8  93\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\lparam: i8  61\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\lparam: i8  %3\lcall: void  putchar\lparam: i8  10\lcall: void  putchar\l%4: i8 = c5_1[0]\lparam: i8  99\lcall: void  putchar\lparam: i8  53\lcall: void  putchar\lparam: i8  91\lcall: void  putchar\lparam: i8  48\lcall: void  putchar\lparam: i8  93\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\lparam: i8  61\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\lparam: i8  %4\lcall: void  putchar\lparam: i8  10\lcall: void  putchar\lt_8 = alloc: i32\l%5: i32 = 100 + 1\l%6: i32 = %5 % 25\l%7: i32 = %6 * 5\l%8: i32 = 3 / 2\l%9: i32 = %7 - %8\lt_8: i32 = %9\lparam: i8  116\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\lparam: i8  61\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\lparam: i32  t_8\lcall: void  putint\lparam: i8  10\lcall: void  putchar\lparam: i32  10\l%10 = call: i32  f2\lt_8: i32 = %10\lparam: i32  t_8\lcall: void  putint\lparam: i8  10\lcall: void  putchar\ls_8 = alloc: i8  10\ls_8[0]: i8 = 49\ls_8[1]: i8 = 50\ls_8[2]: i8 = 51\ls_8[3]: i8 = 52\ls_8[4]: i8 = 53\ls_8[5]: i8 = 54\ls_8[6]: i8 = 0\ls_8[7]: i8 = 0\ls_8[8]: i8 = 0\ls_8[9]: i8 = 0\l%11: &i32 = &(s_8)\lparam: i32  %11\lcall: void  f3\l%12: &i32 = &(c4_1)\lparam: i32  %12\lcall: void  f3\l%13: &i32 = &(c5_1)\lparam: i32  %13\lcall: void  f3\la5_8 = alloc: i32  3\la5_8[0]: i32 = 2\la5_8[1]: i32 = 3\la5_8[2]: i32 = 4\la6_8 = alloc: i32  3\la6_8[0]: i32 = 1\la6_8[1]: i32 = 5\la6_8[2]: i32 = 7\l%14: &i32 = &(a5_8)\l%15: &i32 = &(a6_8)\l%16: i32 = a5_8[1]\lparam: i32  %14\lparam: i32  %15\lparam: i32  %16\l%17 = call: i32  f4\l%18: i32 = %17 + 1\lparam: i8  115\lcall: void  putchar\lparam: i8  117\lcall: void  putchar\lparam: i8  109\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\lparam: i8  43\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\lparam: i8  49\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\lparam: i8  61\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\lparam: i32  %18\lcall: void  putint\lparam: i8  10\lcall: void  putchar\lret: i32  0\l"]
	B0 -> Exit

}

