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
	- function
*/

int main() {
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