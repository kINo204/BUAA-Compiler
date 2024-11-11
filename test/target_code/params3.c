void param_printer(char chs[]) {
    printf("Params: %c%c%c\n", chs[0], chs[1], chs[2]);
}

void param_holder2(char chs[]) {
    param_printer(chs);
}

void param_holder1(char chs[]) {
    param_holder2(chs);
}

int main()
{
    const char chs[3] = "Oh";
    param_holder1(chs);
    return 0;
}