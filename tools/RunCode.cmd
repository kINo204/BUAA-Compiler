@echo off
setlocal

cd ..

echo Compiling ...
java -jar target/compiler.jar

echo Running ...
java -jar marsc.jar mips.txt
echo Program return with value %errorlevel%.

endlocal