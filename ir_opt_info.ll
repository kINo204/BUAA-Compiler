digraph main {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="%1: i32 = 1\l"]
	B0 -> B1 [label="%1 T"]
	B0 -> B3 [label="%1 F"]

    B1 [shape=box xlabel="B1" label="param: i32  4\lcall: void  putint\l%2: i32 = 3\l"]
	B1 -> B2 [label="%2 T"]
	B1 -> B3 [label="%2 F"]

    B2 [shape=box xlabel="B2" label="param: i32  3\lcall: void  putint\lc_6 = alloc: i8\l%3 = call: i8  getchar\lc_6: i8 = %3\lparam: i8  c_6\lcall: void  putchar\l"]
	B2 -> B3

    B3 [shape=box xlabel="B3" label="ret: i32  0\l"]
	B3 -> Exit

}

