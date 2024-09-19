@echo off
setlocal

cd ..\target
if exist "sources.rar" del "sources.rar"
cd ..\src
winrar a -r "..\target\sources" "*"

endlocal