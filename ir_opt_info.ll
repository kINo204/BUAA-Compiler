digraph main {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="a_2 = alloc: i32\la_2: i32 = 10\l%1: i32 = a_2\l"]
	B0 -> B1 [label="%1 T"]
	B0 -> B2 [label="%1 F"]

    B1 [shape=box xlabel="B1" label="a_2: i32 = 1\l"]
	B1 -> B2

    B2 [shape=box xlabel="B2" label="ret: i32  a_2\l"]
	B2 -> Exit

}

