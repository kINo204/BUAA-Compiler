/* Test level-a. Contents:
	- Logic Exp
	- Logic side effects
*/

// Init error msgs.
char e_c[20] = "";

void oerr() {
	int i;
	for (i = 0; i < 20; i = i + 1) {
		if (e_c[i])
			printf("%c", e_c[i]);
	}
}

int side_effect(int num) {
	printf("side effect %d\n", num);
	return 1;
}

int main() {
	printf("22371281\n");

	// A. Logic Exp
	if (1 && 0) e_c[0] = '1';
	if (1 && 0 || 0 && 4) e_c[0] = '1';

	// B. Side effects: 1, 2
	if (0 && side_effect(0)) {} // no exec
	if (1 && side_effect(1)) {} // exec
	if (0 || side_effect(2)) {} // exec
	if (1 || side_effect(3)) {} // no exec

	printf("err caught: ");
	oerr();
	return 0;
}