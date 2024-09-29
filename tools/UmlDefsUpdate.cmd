@echo off
setlocal enabledelayedexpansion

REM 设置源代码目录
set "SOURCE_DIR=..\src"

REM 设置Python脚本路径
set "PYTHON_SCRIPT=PumlDefsUpdate.py"

REM 设置输出文件路径
set "OUTPUT_FILE=uml_defs.puml"

REM 确保输出文件存在
if exist "%OUTPUT_FILE%" rm "%OUTPUT_FILE%"
echo @startuml> "%OUTPUT_FILE%"
echo left to right direction>> "%OUTPUT_FILE%"

REM 遍历目录及其子目录下的所有.java文件
for /r "%SOURCE_DIR%" %%f in (*.java) do (
    REM 调用Python脚本处理每个.java文件
    python "%PYTHON_SCRIPT%" "%%f" >> "%OUTPUT_FILE%"
)

echo Done defs update.
endlocal
