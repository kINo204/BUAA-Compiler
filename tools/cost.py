import re

div = 0
mul = 0
jmp = 0
mem = 0
cal = 0

while True:
	s = input().strip()
	match re.split(r"\s+", s)[0]:
		case '#':
			continue
		case 'move':
			cal+=1
		case 'addu':
			cal+=1
		case 'addi':
			cal+=1
		case 'subu':
			cal+=1
		case 'subiu':
			cal+=1
		case 'mulu':
			mul+=1
		case 'div':
			div+=1
		case 'sll':
			cal+=1
		case 'andi':
			cal+=1
		case 'mfhi':
			cal+=1
		case 'mflo':
			cal+=1
		case 'li':
			cal+=1
		case 'la':
			cal+=1
		case 'lw':
			mem+=1
		case 'lb':
			mem+=1
		case 'sw':
			mem+=1
		case 'sb':
			mem+=1
		case 'slt':
			cal+=1
		case 'sle':
			cal+=1
		case 'sgt':
			cal+=1
		case 'sge':
			cal+=1
		case 'seq':
			cal+=1
		case 'sne':
			cal+=1
		case 'j':
			jmp+=1
		case 'jr':
			jmp+=1
		case 'jal':
			jmp+=1
		case 'bne':
			jmp+=1
		case 'beq':
			jmp+=1
		case 'end':
			break

print(f"div={div}")
print(f"mul={mul}")
print(f"jmp={jmp}")
print(f"mem={mem}")
print(f"cal={cal}")
print(f"total={50*div+3*mul+3*jmp+4*mem+cal}")
