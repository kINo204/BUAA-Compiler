void param_printer(int a) {
    printf("Param: %d\n", a);
}

void param_holder(int a) {
    param_printer(a);
}

int main()
{
    param_holder(6);
    return 0;
}