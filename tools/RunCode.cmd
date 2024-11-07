@echo off
setlocal

cd ..

echo Compiling ...
java -jar target/compiler.jar

echo Running ...
java -jar marsc.jar ic mips.txt>NUL
echo Program exits with return value %errorlevel%.

mv InstructionStatistics.txt target/statistics.txt

endlocal