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

java -jar marsc.jar mips.txt
echo Program exit with value %errorlevel%.
echo.

mv mips.txt target/mips_unopt.txt
mv InstructionStatistics.txt target/statistics.txt
echo statistics
cat target/statistics.txt


echo.
echo [optimized]
java -jar target/compiler.jar O N
if %errorlevel% neq 0 (
	echo Compile error.
	cat error.txt
	exit /B
)
java -jar marsc.jar mips.txt
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