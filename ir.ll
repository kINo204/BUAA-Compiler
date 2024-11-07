fun @1.main:
	%1 = add: i32  1, 3
	if not %1 goto  $l0_if_else
	%2 = mul: i32  2, 2
	%3 = add: i32  %2, 8
	ret: i32  %3
	goto  $l1_if_end

$l0_if_else:
	%4 = sub: i32  0, 0
	ret: i32  %4

$l1_if_end:
	ret: i32  0