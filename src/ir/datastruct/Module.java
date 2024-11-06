package ir.datastruct;

import java.util.ArrayList;

public class Module implements Value {
    // TODO Other elements in Module
    public ArrayList<Function> functions = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Function f : functions) {
            sb.append(f.toString()).append("\n\n");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }
}