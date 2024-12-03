digraph main {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="a_2 = alloc: i32  1\la_2[0]: i32 = 2\lv_2 = alloc: i32\l%1: i32 = a_2[0]\lv_2: i32 = %1\lret: i32  v_2\l"]
	B0 -> Exit

}

