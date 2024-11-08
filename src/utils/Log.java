package utils;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class Log {
    private boolean on = false;
    private final HashMap<Integer, String> errors = new HashMap<>();
    private final HashMap<String, Boolean> config = new HashMap<>();
    private final HashMap<String, Writer> writers = new HashMap<>();

    public void switchLogger(boolean on) { this.on = on; }

    public void addWriter(String writerName, Writer writer) {
        if (writers.containsKey(writerName)) {
            System.err.println(this + " addFileWriter(): writer already exist!");
            return;
        }
        config.put(writerName, true);
        writers.put(writerName, writer);
    }

    public void addFileWriter(String writerName, String fileName) throws IOException {
        if (writers.containsKey(writerName)) {
            System.err.println(this + " addFileWriter(): writer already exist!");
            return;
        }
        config.put(writerName, true);
        writers.put(writerName, new BufferedWriter(new FileWriter(fileName)));
    }

    public void configureWriter(String writerName, Boolean mode) {
        if (!config.containsKey(writerName)) {
            System.err.println(this + " configureWriter(): writer not found!");
            return;
        }
        config.put(writerName, mode);
    }

    public void println(Object o) throws IOException {
        if (!on) return;
        final String str = o.toString() + "\n";
        for (Writer writer : writers.values()) {
            writer.write(str);
        }
    }

    public void print(Object o) throws IOException {
        if (!on) return;
        final String str = o.toString();
        for (Writer writer : writers.values()) {
            writer.write(str);
        }
    }

    public void error(int lineNo, String errCode) {
        errors.putIfAbsent(lineNo, errCode);
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public void executePrintErrors() throws IOException {
        final Object[] lineNumberArray = errors.keySet().toArray();
        Arrays.sort(lineNumberArray);
        for (Object line : lineNumberArray) {
            for (Writer writer : writers.values()) {
                writer.write(line + " " + errors.get((Integer) line) + "\n");
            }
        }
    }

    public void close() throws IOException {
        for (String writerName : writers.keySet()) {
            writers.get(writerName).close();
        }
    }
}
