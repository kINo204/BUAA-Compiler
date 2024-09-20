package io;

import java.io.*;
import java.util.HashMap;

public class Log {
    private final PrintStream stdout = System.out;
    private final PrintStream stderr = System.err;
    private final HashMap<String, Boolean> config = new HashMap<>() {{
        put("stdout", true);
        put("stderr", false);
    }};
    private final HashMap<String, Writer> writers = new HashMap<>();

    public void addFileWriter(String writerName, String fileName) throws IOException {
        if (writers.containsKey(writerName)) {
            stderr.println(this + " addFileWriter(): writer already exist!");
            return;
        }
        config.put(writerName, true);
        writers.put(writerName, new BufferedWriter(new FileWriter(fileName)));
    }

    public void configureWriter(String writerName, Boolean mode) {
        if (!config.containsKey(writerName)) {
            stderr.println(this + " configureWriter(): writer not found!");
            return;
        }
        config.put(writerName, mode);
    }

    public void println(Object o) throws IOException {
        if (config.get("stdout")) stdout.println(o);
        if (config.get("stderr")) stderr.println(o);
        final String str = o.toString() + "\n";
        for (String writerName : writers.keySet()) {
            if (config.get(writerName)) {
                writers.get(writerName).write(str);
            }
        }
    }

    public void flush() throws IOException {
        for (String writerName : writers.keySet()) {
            writers.get(writerName).flush();
        }
    }

    public void close() throws IOException {
        for (String writerName : writers.keySet()) {
            writers.get(writerName).close();
        }
    }
}
