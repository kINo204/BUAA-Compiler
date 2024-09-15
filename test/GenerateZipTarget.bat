@echo off
if exist test_files.rar del test_files.rar

for %%f in (testfile*.c) do (
    for /f "skip=14 delims=" %%a in (%%f) do (
        set "str=%%a"
        setlocal enabledelayedexpansion
        echo(!str!>> "%%~nf.txt"
        endlocal
    )
)

"winrar" a "test_files" testfile*.txt input*.txt output*.txt

del testfile*.txt
echo Done. All files have been processed and packed into a ZIP file.
pause