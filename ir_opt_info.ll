digraph de {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="ret\l"]
	B0 -> Exit

}

digraph keke {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="%1: i32 = i_3\l%2: i32 = j_3\l%3 = add: i32  %1, %2\li_3: i32 = %3\lret: i32  0\l"]
	B0 -> Exit

}

digraph jian {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="x_4 = alloc: i32\ly_4 = alloc: i32\lz_4 = alloc: i32\l%1 = call: i32  getint\lx_4: i32 = %1\l%2 = call: i32  getint\ly_4: i32 = %2\l%3: i32 = x_4\l%4: i32 = y_4\l%5 = sub: i32  %3, %4\lz_4: i32 = %5\l%6: i32 = z_4\lret: i32  %6\l"]
	B0 -> Exit

}

digraph main {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="a_5 = alloc: i32\lb_5 = alloc: i32\lc_5 = alloc: i32\ld_5 = alloc: i32\le_5 = alloc: i32\lf_5 = alloc: i32\lg_5 = alloc: i32\lg_5: i32 = 1\lh_5 = alloc: i32\lj_5 = alloc: i32\lk_5 = alloc: i32\ll_5 = alloc: i32\lo_5 = alloc: i32\lo_5: i32 = -1\li_5 = alloc: i32\li_5: i32 = 2\ln_5 = alloc: i32\lm_5 = alloc: i32\lflag_5 = alloc: i32\lflag_5: i32 = 0\l%1 = call: i32  getint\ln_5: i32 = %1\l"]
	B0 -> B1

    B1 [shape=box xlabel="B1" label="%2: i32 = i_5\l%3: i32 = n_5\l%4 = lss: i32  %2, %3\l"]
	B1 -> B2 [label="%4 T"]
	B1 -> B5 [label="%4 F"]

    B2 [shape=box xlabel="B2" label="%5: i32 = n_5\l%6: i32 = i_5\l%7 = mod: i32  %5, %6\lm_5: i32 = %7\l%8: i32 = m_5\l%9 = eql: i32  %8, 0\l"]
	B2 -> B3 [label="%9 T"]
	B2 -> B4 [label="%9 F"]

    B3 [shape=box xlabel="B3" label="flag_5: i32 = 1\lparam: i8  48\lcall: void  putchar\lparam: i8  10\lcall: void  putchar\l"]
	B3 -> B4

    B4 [shape=box xlabel="B4" label="%10: i32 = i_5\l%11 = add: i32  %10, 1\li_5: i32 = %11\l"]
	B4 -> B1

    B5 [shape=box xlabel="B5" label="%12 = call: i32  jian\lc_5: i32 = %12\l%13: i32 = c_5\lparam: i32  %13\lcall: void  putint\lparam: i8  10\lcall: void  putchar\l%14: i32 = c_5\l%15 = add: i32  %14, 1\ld_5: i32 = %15\l%16: i32 = c_5\l%17 = mul: i32  %16, 2\le_5: i32 = %17\l%18: i32 = e_5\l%19 = lss: i32  %18, 5\l"]
	B5 -> B6 [label="%19 T"]
	B5 -> B7 [label="%19 F"]

    B6 [shape=box xlabel="B6" label="%20: i32 = c_5\l%21 = mod: i32  %20, 2\lf_5: i32 = %21\l"]
	B6 -> B8

    B7 [shape=box xlabel="B7" label="%22: i32 = c_5\l%23 = div: i32  %22, 2\lf_5: i32 = %23\l"]
	B7 -> B8

    B8 [shape=box xlabel="B8" label="%24: i32 = f_5\l%25 = neq: i32  %24, 0\l"]
	B8 -> B9 [label="%25 T"]
	B8 -> B10 [label="%25 F"]

    B9 [shape=box xlabel="B9" label="%26: i32 = g_5\l%27 = add: i32  %26, 1\lg_5: i32 = %27\l"]
	B9 -> B10

    B10 [shape=box xlabel="B10" label="%28: i32 = i_5\l%29: i32 = j_5\l%30 = add: i32  %29, 1\l%31 = add: i32  %28, %30\lo_5: i32 = %31\l"]
	B10 -> B11

    B11 [shape=box xlabel="B11" label=""]
	B11 -> B11 [label="0 T"]
	B11 -> B12 [label="0 F"]

    B12 [shape=box xlabel="B12" label=""]
	B12 -> B13 [label="1 T"]
	B12 -> B13 [label="1 F"]

    B13 [shape=box xlabel="B13" label="%32: i32 = c_5\l%33: i32 = d_5\l%34 = eql: i32  %32, %33\l"]
	B13 -> B14 [label="%34 T"]
	B13 -> B19 [label="%34 F"]

    B14 [shape=box xlabel="B14" label="%35: i32 = d_5\l%36: i32 = e_5\l%37 = geq: i32  %35, %36\l"]
	B14 -> B15 [label="%37 T"]
	B14 -> B19 [label="%37 F"]

    B15 [shape=box xlabel="B15" label="%38: i32 = e_5\l%39: i32 = f_5\l%40 = leq: i32  %38, %39\l"]
	B15 -> B16 [label="%40 T"]
	B15 -> B19 [label="%40 F"]

    B16 [shape=box xlabel="B16" label="%41: i32 = f_5\l%42: i32 = g_5\l%43 = neq: i32  %41, %42\l"]
	B16 -> B17 [label="%43 T"]
	B16 -> B19 [label="%43 F"]

    B17 [shape=box xlabel="B17" label="%44: i32 = c_5\l%45 = gre: i32  %44, 1\l"]
	B17 -> B18 [label="%45 T"]
	B17 -> B19 [label="%45 F"]

    B18 [shape=box xlabel="B18" label="a_5: i32 = 1\l"]
	B18 -> B19

    B19 [shape=box xlabel="B19" label="%46: i32 = a_5\l%47: i32 = b_5\lparam: i32  %46\lparam: i32  %47\l%48 = call: i32  keke\l%49: i32 = d_5\l%50: i32 = e_5\l%51: i32 = f_5\l%52: i32 = g_5\lparam: i32  %49\lcall: void  putint\lparam: i8  10\lcall: void  putchar\lparam: i32  %50\lcall: void  putint\lparam: i8  10\lcall: void  putchar\lparam: i32  %51\lcall: void  putint\lparam: i8  10\lcall: void  putchar\lparam: i32  %52\lcall: void  putint\lparam: i8  10\lcall: void  putchar\lparam: i8  49\lcall: void  putchar\lparam: i8  57\lcall: void  putchar\lparam: i8  49\lcall: void  putchar\lparam: i8  56\lcall: void  putchar\lparam: i8  50\lcall: void  putchar\lparam: i8  54\lcall: void  putchar\lparam: i8  50\lcall: void  putchar\lparam: i8  48\lcall: void  putchar\lparam: i8  10\lcall: void  putchar\lparam: i8  49\lcall: void  putchar\lparam: i8  57\lcall: void  putchar\lparam: i8  49\lcall: void  putchar\lparam: i8  56\lcall: void  putchar\lparam: i8  50\lcall: void  putchar\lparam: i8  54\lcall: void  putchar\lparam: i8  50\lcall: void  putchar\lparam: i8  48\lcall: void  putchar\lparam: i8  10\lcall: void  putchar\lparam: i8  49\lcall: void  putchar\lparam: i8  57\lcall: void  putchar\lparam: i8  49\lcall: void  putchar\lparam: i8  56\lcall: void  putchar\lparam: i8  50\lcall: void  putchar\lparam: i8  54\lcall: void  putchar\lparam: i8  50\lcall: void  putchar\lparam: i8  48\lcall: void  putchar\lparam: i8  10\lcall: void  putchar\lret: i32  0\l"]
	B19 -> Exit

}

