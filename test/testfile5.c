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

/* Test level-c1. Contents:
	- global decl/func
	- function as exp
	- embedded blocks
*/

// A. Global var decl
const int ci0 = 1;
const int ci1 = 2, ci2 = 4;

const char cc0 = '1';
const char cc1 = '2', cc2 = '4';

int vi0 = 1;
int vi1 = 2, vi2 = 4;

char vc0 = '1';
char vc1 = '2', vc2 = '4';

int add(int a, char b) {
	return a + b;
}

int main() {
	printf("22371281\n");
	printf("err caught: ");
	
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

	if (ci0 + ci1 + ci2 != 7) e_c1 = '1';
	if (cc0 + (cc1 - '0') + (cc2 - '0') != '7') e_c2 = '2';
	if (vi0 + vi1 + vi2 != 7) e_c3 = '3';
	if (vc0 + (vc1 - '0') + (vc2 - '0') != '7') e_c4 = '4';

	// B. func
	int c1 = add(1, '0');
	if (c1 != '1') e_c5 = '5';

	// C. Block
	int b0 = 0;
	{
		int b0 = 5;
		if (b0 != 5) e_c6 = '6';
	}
	if (b0 != 0) e_c6 = '6';
	
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