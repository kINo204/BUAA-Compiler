void param_printer(char chs[]) {
    printf("Params: %c%c%c\n", chs[0], chs[1], chs[2]);
}

void param_holder(char chs[]) {
    param_printer(chs);
}

int main()
{
    const char chs[3] = "Oh";
    param_holder(chs);
    return 0;
}