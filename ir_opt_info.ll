define fun func:
	if false 1 goto  $L0

$L0:
	ret

define fun main:
	if false 1 goto  $L1

$L1:
	ret: i32  0