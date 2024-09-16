#include <stdio.h>
int getchar(){
	char c; 
	scanf("%c",&c); 
	return (int)c; 
}

int getint(){
	int t;
	scanf("%d",&t);
	while(getchar()!='\n');
	return t;
}

/* Test level-c0. Contents:
	- MainFuncDef
	- InStmt
	- OutStmt
	- comments
	- Decl
	- AddExp, EqExp, RelExp
	- Stmt (partial)
*/

// MainFuncDef
int main()
{
	// Init error msgs.
	char e_c1 = '\0';
	char e_c2 = '\0';
	char e_c3 = '\0';
	char e_c4 = '\0';
	char e_c5 = '\0';
	char e_c6 = '\0';
	char e_c7 = '\0';
	char e_c8 = '\0';
	char e_c9 = '\0';
	char e_c10 = '\0';
	char e_c11 = '\0';
	char e_c12 = '\0';
	char e_c13 = '\0';
	char e_c14 = '\0';
	char e_c15 = '\0';
	char e_c16 = '\0';

	// A. In/Out Stmt
	printf("22371281\n");
	int a;
	a = getint();
	printf("%d\n", a);
	char b;
	b = getchar();
	printf("%c\n", b);
	printf("err caught: ");

	// B. Comments
	// this should not be executed:
	// printf("comment test failed! double-slash executed.\n");
	/* printf("comment test failed! slash-star executed.\n"); */

	// C. Decl
	const int ci0 = 1;
	const int ci1 = 2, ci2 = 4;
	if (ci0 + ci1 + ci2 != 7) e_c1 = '1';

	const char cc0 = '1';
	const char cc1 = '2', cc2 = '4';
	if (cc0 + (cc1 - '0') + (cc2 - '0') != '7') e_c2 = '2';

	int vi0 = 1;
	int vi1 = 2, vi2 = 4;
	if (vi0 + vi1 + vi2 != 7) e_c3 = '3';

	char vc0 = '1';
	char vc1 = '2', vc2 = '4';
	if (vc0 + (vc1 - '0') + (vc2 - '0') != '7') e_c4 = '4';

	// no assign
	int vin0;
	int vin1, vin2;

	char vcn0;
	char vcn1, vcn2;

	// D. AddExp (Exp, ConstExp)
	int v0 = 1;
	int exp0 = +v0 * '0' / 3 % 7 + 55 - -1;
	int exp1 = (+v0 * '0' / 3 % 7) + 55 - -1;
	if (exp0 != 58) e_c6 = '6';
	if (exp1 != 58) e_c7 = '7';

	// E. EqExp, RelExp (Note: condition unassignable to var)
	if (!1) e_c8 = '8';
	if (2 < 1) e_c9 = '9';
	if (2 <= 1) e_c10 = 'a';
	if (1 > 2) e_c11 = 'b';
	if (1 >= 2) e_c12 = 'c';
	if (1 != 1) e_c13 = 'd';
	if (1 == 2) e_c14 = 'e';

	// F. Stmt
	int v1; v1 = 1;
	;
	v1;

	int v2 = 0;
	if (0) {}
	else if (0) {}
	else v2 = 2;
	if (v1 + v2 != 3) e_c15 = 'f';

	int tmp;
	for (;;) {
		break;
		printf("cannot break from loop\n");
		return -1; // Can't break!
	}
	for (;;tmp=0) {
		break;
		printf("cannot break from loop\n");
		return -1; // Can't break!
	}
	for (;1;tmp=0) {
		break;
		printf("cannot break from loop\n");
		return -1; // Can't break!
	}
	for (;1;) {
		break;
		printf("cannot break from loop\n");
		return -1; // Can't break!
	}
	for (tmp=0;;) {
		break;
		printf("cannot break from loop\n");
		return -1; // Can't break!
	}
	for (tmp=0;1;) {
		break;
		printf("cannot break from loop\n");
		return -1; // Can't break!
	}
	for (tmp=0;;tmp=0) {
		break;
		printf("cannot break from loop\n");
		return -1; // Can't break!
	}

	int i;
	for (i = 0; i < 1; i = i + 1) {
		continue;
		printf("won't continue in loop\n");
		return -1; // Can't continue!
	}

	int v3 = 0;
	for (;;) {
		v3 = 1;
		break;
	}
	int v4 = 0;
	for (i = 0; i < 2; i = i + 1) {
		v4 = v4 + 1;
	}
	if (v3 + v4 != 3) e_c16 = 'g';

	// Error msg output.
	printf("%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c\n"
		, e_c1
		, e_c2
		, e_c3
		, e_c4
		, e_c5
		, e_c6
		, e_c7
		, e_c8
		, e_c9
		, e_c10
		, e_c11
		, e_c12
		, e_c13
		, e_c14
		, e_c15
		, e_c16
		);
	return 0;
}