# BUAA Compiler Project

![image](https://github.com/user-attachments/assets/309ba01b-dab6-48b4-a79b-4803ca7b5b42)

This is a repository containing labs for BUAA compiler course. DO NOT USE THE CODE DIRECTLY!

The compiler is written in Java, implementing a recursive-descent frontend, a mid part
with customized 3-address IR, and a backend for MIPS architecture(running on "marsc.jar").

## Usage

At working directory, run the script `./tools/run.cmd`. This will compile
the source program in `testfile.txt` and run the generated `mips.txt` through `marsc.jar`.

You can dis-rem comments in `run.cmd` to enable comparing running with unoptimized compiler.
The compiler configs is controlled by two CLI args: `java -jar compiler.jar O P`, indicating
enabling optimizing & runtime profiling info. Replace `O` or `P` with `N` will disable the
corresponding function.

A `dot` command line tool for graphviz is needed as well. After each run you may find the
control flow graph of the *optimized* program in `./target/cfg/`.

All runnable tools lies within `./tools/`, use at ease.
