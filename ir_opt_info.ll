digraph func {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="%1 = eql: i32  n_2, 0\l"]
	B0 -> B1 [label="%1 T"]
	B0 -> B2 [label="%1 F"]

    B1 [shape=box xlabel="B1" label="ret: i32  n_2\l"]
	B1 -> Exit

    B2 [shape=box xlabel="B2" label="%2 = sub: i32  n_2, 1\ln_2: i32 = %2\l"]
	B2 -> B0

}

digraph main {
    graph [dpi=320]
    Entry [shape=ellipse]
    Exit [shape=ellipse]

	Entry -> B0

    B0 [shape=box xlabel="B0" label="param: i32  4\l%1 = call: i32  func\lret: i32  0\l"]
	B0 -> Exit

}

