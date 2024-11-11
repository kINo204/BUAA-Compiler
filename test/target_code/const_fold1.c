int main()
{
    const int vn = 1;
    const char vc = 'A';
    const int vna[2] = { 1, 2 };
    const char vca[2] = { 'A', 'B' };
    int res_n = vna[1];
    char res_ch = vca[1] + 2;
    printf("n = %d, ch = %c\n", res_n, res_ch);
    return 0;
}