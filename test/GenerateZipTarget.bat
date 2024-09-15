@echo off
setlocal enabledelayedexpansion
if exist test_files.rar del test_files.rar

for %%f in (testfile*.txt) do (
    for /f "skip=14 delims=" %%a in (%%f) do (
        echo %%a >> "%%~nf.tmp"
    )
)


"winrar" a "test_files" testfile*.tmp input*.txt output*.txt

del *.tmp
echo Done. All files have been processed and packed into a ZIP file.
pause