package io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class Log {
    private final BufferedWriter stdout = new BufferedWriter(new OutputStreamWriter(System.out));
    private final BufferedWriter stderr = new BufferedWriter(new OutputStreamWriter(System.err));
    private final HashMap<String, Boolean> config = new HashMap<>() {{
        put("stdout", true);
        put("stderr", false);
    }};
    private final HashMap<String, BufferedWriter> writers = new HashMap<>() {{
        put("stdout", stdout);
        put("stderr", stderr);
    }};

    public void addFileWriter(String writerName, String fileName) throws IOException {
        if (writers.containsKey(writerName)) {
            System.err.println(this + " addFileWriter(): writer already exist!");
            return;
        }
        config.put(writerName, true);
        writers.put(writerName, new BufferedWriter(new FileWriter(fileName)));
    }

    public void configureWriter(String writerName, Boolean mode) {
        if (!writers.containsKey(writerName)) {
            System.err.println(this + " configureWriter(): writer not found!");
            return;
        }
        config.replace(writerName, mode);
    }

    public void write(String str) throws IOException {
        for (String writerName : writers.keySet()) {
            if (config.get(writerName)) {
                writers.get(writerName).write(str);
            }
        }
    }

    public void print(Object o) throws IOException {
        final String str = o.toString();
        for (String writerName : writers.keySet()) {
            if (config.get(writerName)) {
                writers.get(writerName).write(str);
            }
        }
    }

    public void println(Object o) throws IOException {
        final String str = o.toString() + "\n";
        for (String writerName : writers.keySet()) {
            if (config.get(writerName)) {
                writers.get(writerName).write(str);
            }
        }
    }

    public void close() throws IOException {
        for (String writerName : writers.keySet()) {
            writers.get(writerName).close();
        }
    }
}
