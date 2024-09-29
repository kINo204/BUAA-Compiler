import re

# 定义正则表达式模式
class_pattern = re.compile(r'^\s*public\s+class\s+(\w+)\s*{.*')
attribute_pattern = re.compile(r'^\s*(public|private)\s*(?:final\s*|static\s*)*([\w\d]+(?:<[\w\d.]+(?:,\s*[\w\d.]+)*>)?)\s*([\w\d]+).*;')
method_pattern = re.compile(r'^\s*(public|private)\s*([a-zA-Z0-9_]+)\s+(\w+)\s*\((.*)\).*')

# 用于存储类名和成员变量及方法
class_info = {}

def process_java_file(file_path):
	with open(file_path, 'r') as file:
		for line in file.readlines():
			# 查找类名
			class_match = class_pattern.search(line)
			if class_match:
				class_name = class_match.group(1)
				class_info[class_name] = {'attributes': [], 'methods': []}
			# 查找成员变量
			attribute_match = attribute_pattern.search(line)
			if attribute_match:
				visibility, type, attribute = attribute_match.groups()
				class_info[class_name]['attributes'].append(f"{'+' if visibility=='public' else '-'} {attribute} : {type}")
			# 查找方法
			method_match = method_pattern.search(line)
			if method_match:
				visibility, return_type, method_name, params = method_match.groups()
				class_info[class_name]['methods'].append(f"{'+' if visibility=='public' else '-'} {method_name}({params}) : {return_type}")

def generate_plantuml(class_info):
	uml_output = ""
	for class_name, members in class_info.items():
		uml_output += f"class {class_name}\n"
		for attr in members['attributes']:
			uml_output += f"{class_name} : {attr}\n"
		for method in members['methods']:
			uml_output += f"{class_name} : {method}\n"
		uml_output += "\n"
	return uml_output

if __name__ == "__main__":
	import sys
	if len(sys.argv) < 2:
		print("Usage: python script.py <java_file>")
		sys.exit(1)
	
	java_file_path = sys.argv[1]
	process_java_file(java_file_path)
	uml_diagram = generate_plantuml(class_info)
	print(uml_diagram)