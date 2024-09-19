import os
import subprocess
import filecmp

# 定义日志函数
def log_message(message, testcase):
    print(f"[{testcase}] {message}")

# 定义执行JAR文件的函数
def run_jar(input_file, output_file):
    try:
        # 构建命令行指令
        cmd = ['java', '-jar', 'target/exec.jar', input_file, output_file]
        # 执行命令
        subprocess.run(cmd, check=True)
        log_message("JAR executed successfully.", os.path.basename(os.getcwd()))
    except subprocess.CalledProcessError as e:
        log_message(f"JAR execution failed with error: {e}", os.path.basename(os.getcwd()))

# 定义比较文件内容的函数
def compare_files(file1, file2):
    if filecmp.cmp(file1, file2, shallow=False):
        return True
    else:
        return False

# 遍历test下的每个子目录
for subdir in next(os.walk('test'))[1]:
    testcase_path = os.path.join('test', subdir)
    for testcase in next(os.walk(testcase_path))[1]:
        testcase_dir = os.path.join(testcase_path, testcase)
        input_file = os.path.join(testcase_dir, 'testfile.txt')
        output_file = os.path.join(testcase_dir, 'output.txt')  # 假设输出文件名为output.txt
        ans_file = os.path.join(testcase_dir, 'ans.txt')

        # 运行JAR文件
        run_jar(input_file, output_file)

        # 比较输出文件和答案文件
        if compare_files(output_file, ans_file):
            log_message("Output matches the expected answer.", testcase)
        else:
            log_message("Output does not match the expected answer.", testcase)
