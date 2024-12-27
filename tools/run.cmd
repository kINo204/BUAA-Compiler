@echo off
setlocal

cd ..

rem echo.
rem echo [unoptimized + plain-trans]
rem java -jar target/unoptimized.jar>nul
rem if %errorlevel% neq 0 (
rem 	echo Compile error.
rem 	cat error.txt
rem 	exit /B
rem )

rem java -jar marsc.jar mips.txt
rem echo Program exit with value %errorlevel%.
rem echo.

rem mv mips.txt target/mips_unopt.txt
rem mv InstructionStatistics.txt target/statistics.txt
rem echo statistics
rem cat target/statistics.txt


echo.
echo [optimized + real-trans]
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