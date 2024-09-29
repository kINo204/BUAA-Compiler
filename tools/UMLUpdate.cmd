@echo off
setlocal

call UMLDefsUpdate.cmd

if exist "..\doc\Arch.puml" del "..\doc\Arch.puml"
echo. > "..\doc\Arch.puml"
cat "uml_defs.puml">> "..\doc\Arch.puml"
cat "..\doc\relations.puml">> "..\doc\Arch.puml"
echo Done updating.

endlocal
