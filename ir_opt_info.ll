digraph fib {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="%1: i32 = i_2 == 1\l"]
	B0 -> B1 [label="%1 T"]
	B0 -> B2 [label="%1 F"]

    B1 [shape=box xlabel="B1" label="ret: i32  1\l"]
	B1 -> Exit

    B2 [shape=box xlabel="B2" label="%2: i32 = i_2 == 2\l"]
	B2 -> B3 [label="%2 T"]
	B2 -> B4 [label="%2 F"]

    B3 [shape=box xlabel="B3" label="ret: i32  2\l"]
	B3 -> Exit

    B4 [shape=box xlabel="B4" label="%3: i32 = i_2 - 1\lparam: i32  %3\l%4 = call: i32  fib\l%5: i32 = i_2 - 2\lparam: i32  %5\l%6 = call: i32  fib\l%7: i32 = %4 + %6\lret: i32  %7\l"]
	B4 -> Exit

}

digraph main {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="i_5 = alloc: i32\lj_5 = alloc: i32\l%1 = call: i32  getint\li_5: i32 = %1\l%2 = call: i32  getint\lj_5: i32 = %2\l%3: i32 = i_5 * j_5\l%4: i32 = 0 - %3\lparam: i32  4\l%5 = call: i32  fib\l%6: i32 = %4 * %5\l%7: i32 = a_1[1]\l%8: i32 = %7\l%10: i32 = %6\l%11: i32 = %10 + %8\l%12: i32 = %11\l%13: i32 = %12 / 5\li_5: i32 = %13\lparam: i32  5\l%17 = call: i32  fib\l%18: i32 = %17 + 2\lparam: i32  %18\l%19 = call: i32  fib\l%20: i32 = 1197 - %19\l%21: i32 = %20 + -10091\lj_5: i32 = %21\lk_5 = alloc: i32\lk_5: i32 = -6\l%22: i32 = a_1[0]\l%23: i32 = i_5 * i_5\l%24: i32 = %22 + %23\la_1[0]: i32 = %24\l%25: i32 = a_1[1]\l%26: i32 = i_5 * i_5\l%27: i32 = %25 + %26\la_1[1]: i32 = %27\l%28: i32 = a_1[2]\l%29: i32 = i_5 * i_5\l%30: i32 = %28 + %29\la_1[2]: i32 = %30\l%31: i32 = a_1[3]\l%32: i32 = i_5 * i_5\l%33: i32 = %31 + %32\la_1[3]: i32 = %33\l%34: i32 = a_1[4]\l%35: i32 = i_5 * i_5\l%36: i32 = %34 + %35\la_1[4]: i32 = %36\l%37: i32 = a_1[5]\l%38: i32 = i_5 * i_5\l%39: i32 = %37 + %38\la_1[5]: i32 = %39\l%40: i32 = a_1[6]\l%41: i32 = i_5 * i_5\l%42: i32 = %40 + %41\la_1[6]: i32 = %42\l%43: i32 = a_1[7]\l%44: i32 = i_5 * i_5\l%45: i32 = %43 + %44\la_1[7]: i32 = %45\l%46: i32 = a_1[8]\l%47: i32 = i_5 * i_5\l%48: i32 = %46 + %47\la_1[8]: i32 = %48\l%49: i32 = a_1[9]\l%50: i32 = i_5 * i_5\l%51: i32 = %49 + %50\la_1[9]: i32 = %51\li_5: i32 = 0\l"]
	B0 -> B1

    B1 [shape=box xlabel="B1" label="%52: i32 = i_5 < 10\l"]
	B1 -> B2 [label="%52 T"]
	B1 -> B3 [label="%52 F"]

    B2 [shape=box xlabel="B2" label="%53: i32 = a_1[i_5]\lparam: i32  %53\lcall: void  putint\lparam: i8  44\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\l%54: i32 = i_5 + 1\li_5: i32 = %54\l"]
	B2 -> B1

    B3 [shape=box xlabel="B3" label="param: i8  10\lcall: void  putchar\lparam: i32  i_5\lcall: void  putint\lparam: i8  44\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\lparam: i32  j_5\lcall: void  putint\lparam: i8  44\lcall: void  putchar\lparam: i8  32\lcall: void  putchar\lparam: i32  k_5\lcall: void  putint\lparam: i8  10\lcall: void  putchar\lret: i32  0\l"]
	B3 -> Exit

}

