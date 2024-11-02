package ir.datastruct;

import java.util.ArrayList;

class Module implements Value {
    // TODO Other elements in Module
    ArrayList<Function> functions = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Function f : functions) {
            sb.append(f.toString()).append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}