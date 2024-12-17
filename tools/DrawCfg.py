import subprocess

# 定义dot文件的内容
dot_content = ""
with open("../ir_opt_info.ll", 'r') as file:
    dot_content = file.read()

# 将dot内容分割成单独的digraph
graphs = dot_content.strip().split('digraph')[1:]

# 遍历每个digraph并生成PNG文件
for i, graph in enumerate(graphs):
    # 移除前后空白字符
    graph = graph.strip()
    # 找到digraph的名称
    name = graph.split(' ')[0]
    # 创建临时dot文件
    with open(f'{name}.dot', 'w') as f:
        f.write(f'digraph {graph}')
    # 使用dot命令生成PNG文件
    subprocess.run(['dot', '-Tpng', f'{name}.dot',
        '-o', f'../target/cfgs/{name}.png'])
    # 删除临时dot文件
    subprocess.run(['rm', f'{name}.dot'])