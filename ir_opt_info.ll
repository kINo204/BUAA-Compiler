digraph main {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="a_2 = alloc: i32\lb_2 = alloc: i32\lb_2: i32 = x_1\la_2: i32 = b_2\lret: i32  a_2\l"]
	B0 -> Exit

}

