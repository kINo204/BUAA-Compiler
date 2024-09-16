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

/* Test level-b0. Contents:
 	- array basics
*/

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

void oerr() {
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
}

int pong_int(int a) {
	return a;
}

char pong_char(char c) {
	return c;
}

int two_sum(int arr[]) {
	return arr[0] + arr[1];
}

int main() {
	printf("22371281\n");
	printf("err caught: ");

	// A. Array as var
	int anon[2];
	int ai[2] = {1, 2};
	const int cai[2] = {1, 2};
	char ac0[3] = {'1', '2'};
	const char cac0[3] = {'1', '2'};
	char ac1[3] = "12";
	const char cac1[3] = "12";
	if (ai[0] != 1) e_c1 = '1';
	if (ai[1] != 2) e_c1 = '1';
	if (cai[0] != 1) e_c2 = '2';
	if (cai[1] != 2) e_c2 = '2';
	if (ac0[0] != '1') e_c3 = '3';
	if (ac0[1] != '2') e_c3 = '3';
	if (ac0[2] != '\0') e_c3 = '3';
	if (cac0[0] != '1') e_c4 = '4';
	if (cac0[1] != '2') e_c4 = '4';
	if (cac0[2] != '\0') e_c4 = '4';
	if (ac1[0] != '1') e_c5 = '5';
	if (ac1[1] != '2') e_c5 = '5';
	if (ac1[2] != '\0') e_c5 = '5';
	if (cac1[0] != '1') e_c6 = '6';
	if (cac1[1] != '2') e_c6 = '6';
	if (cac1[2] != '\0') e_c6 = '6';

	// B. Array as func param
	if (pong_int(ai[0]) != 1) e_c7 = '7';
	if (pong_char(ac0[1]) != '2') e_c7 = '7';
	if (pong_char(ac1[2]) != '\0') e_c7 = '7';

	if (two_sum(ai) != 3) e_c8 = '8';
	if (two_sum(cai) != 3) e_c8 = '8';

	oerr();
	return 0;
}