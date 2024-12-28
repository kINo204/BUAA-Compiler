digraph main {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="%1: i32 = 1\l"]
	B0 -> B1 [label="%1 T"]
	B0 -> B4 [label="%1 F"]

    B1 [shape=box xlabel="B1" label="b_4 = alloc: i32  2\lb_4[0]: i32 = 2\lb_4[1]: i32 = 4\l"]
	B1 -> B2

    B2 [shape=box xlabel="B2" label="%2: i32 = b_4[1]\l%3: i32 = %2 <= 6\l"]
	B2 -> B3 [label="%3 T"]
	B2 -> B4 [label="%3 F"]

    B3 [shape=box xlabel="B3" label="%4: i32 = b_4[1]\lparam: i32  %4\lcall: void  putint\l%5: i32 = b_4[1]\l%6: i32 = %5 + 1\lb_4[1]: i32 = %6\l"]
	B3 -> B2

    B4 [shape=box xlabel="B4" label="ret: i32  0\l"]
	B4 -> Exit

}

