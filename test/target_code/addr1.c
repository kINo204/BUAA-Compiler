int gvi[3] = {};
char gvc[3] = "";
const int gci[3] = {};
const char gcc[3] = "";

int ai(int arr[])
{
    return arr;
}

int ac(char arr[])
{
    return arr;
}

int main()
{
    int lvi[3] = {};
    char lvc[3] = "";
    const int lci[3] = {};
    const char lcc[3] = "";

    printf("addr local: %d, %d, %d, %d\n",
        ai(lvi), ac(lvc), ai(lci), ac(lcc));
    printf("addr globl: %d, %d, %d, %d\n",
        ai(gvi), ac(gvc), ai(gci), ac(gcc));
    return 0;
}