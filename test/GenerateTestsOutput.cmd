@echo off
setlocal enabledelayedexpansion

:: 遍历当前目录下所有以testfile为前缀的.c文件
for %%i in (testfile*.txt) do (
    set "filename=%%~ni"
    set "extension=%%~xi"

    :: 提取testfile之后的部分
    set "remaining="
    for /f "tokens=1 delims=testfile" %%a in ("!filename!") do set "remaining=%%a"

    :: 拼接新的可执行文件名和输出文件名
    set "exefilename=testfile!remaining!.exe"
    set "inputfilename=input!remaining!.txt"
    set "outputfilename=output!remaining!.txt"

    :: 使用gcc编译源文件
    gcc -x c "%%i" -o "!exefilename!"

    :: 检查gcc编译是否成功
    if !errorlevel! equ 0 (
        :: 编译成功，执行可执行文件并将输出重定向到output文件
        !exefilename! < "!inputfilename!" > "!outputfilename!"
        echo Execution output saved for: !exefilename!
        
        :: 清理生成的可执行文件
        del "!exefilename!"
    ) else (
        echo Compilation failed for: %%i
    )
)

echo All files processed.
pause
