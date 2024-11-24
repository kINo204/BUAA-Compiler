@echo off
setlocal

cd ..

echo.
echo [unoptimized]
java -jar target/unoptimized.jar>nul
if %errorlevel% neq 0 (
	echo Compile error.
	cat error.txt
	exit /B
)

java -jar marsc.jar ic mips.txt>nul

mv InstructionStatistics.txt target/statistics.txt
echo statistics
cat target/statistics.txt


echo.
echo [optimized]
java -jar target/compiler.jar
if %errorlevel% neq 0 (
	echo Compile error.
	cat error.txt
	exit /B
)
java -jar marsc.jar ic mips.txt
echo Program exit with value %errorlevel%.
echo.
mv InstructionStatistics.txt target/statistics.txt
echo.
echo statistics:
cat target/statistics.txt

cd target/cfgs
rm -f *.png

cd ../..
cd tools
python DrawCfg.py

endlocal