@echo off
setlocal

cd ..

echo compiling ...
java -jar target/compiler.jar
if %errorlevel% neq 0 (
	echo Compile error.
	cat error.txt
	exit /B
)

echo running ...
java -jar marsc.jar ic mips.txt
echo exit value %errorlevel%.

echo.
echo statistics:
cat target/statistics.txt

mv InstructionStatistics.txt target/statistics.txt

endlocal