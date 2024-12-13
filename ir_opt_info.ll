digraph f {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="*(a_2)[0]: i32 = 1\lb_2 = alloc: i32\l%1: i32 = 1\lb_2: i32 = 1\lret\l"]
	B0 -> Exit

}

digraph main {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="a_3 = alloc: i32  2\la_3[0]: i32 = 1\l%1: &i32 = &(a_3)\lparam: i32  %1\lcall: void  f\lb_3 = alloc: i32\l%2: i32 = a_3[0]\lb_3: i32 = %2\lc_3 = alloc: i32\lc_3: i32 = b_3\lret: i32  c_3\l"]
	B0 -> Exit

}

