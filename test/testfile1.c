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

/* Test level-a. Contents:
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