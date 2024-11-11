@echo off
setlocal

cd ..

echo Compiling ...
java -jar target/compiler.jar
if %errorlevel% neq 0 (
	echo Compile error.
	cat error.txt
	exit /B
)

echo Running ...
java -jar marsc.jar ic sp mips.txt
echo Program exit with value %errorlevel%.

echo.

mv InstructionStatistics.txt target/statistics.txt
echo statistics:
cat target/statistics.txt


endlocal