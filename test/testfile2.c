/* Test level-b1. Contents:
*/

// Init error msgs.
char e_c[20] = "";

void oerr() {
	int i;
	for (i = 0; i < 20; i = i + 1) {
		printf("%c", e_c[i]);
	}
}

int main() {
	oerr();
	return 0;
}