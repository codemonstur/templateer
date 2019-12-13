package templateer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum IO {;

    public static String toExtension(final File inFile) {
        final String name = inFile.getName();
        final int index = name.indexOf('.');
        return index == -1 ? "" : name.substring(index);
    }

    public static Map<String, Object> loadModel(final String modelFile) throws IOException {
        return loadFileInto(modelFile, loadEnvironmentInto(new HashMap<>()));
    }

    public static Map<String, Object> loadFileInto(final String file, final Map<String, Object> model) throws IOException {
        try (final var reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final int equalsOffset = line.indexOf('=');
                if (equalsOffset == -1) continue;

                model.put(line.substring(0, equalsOffset).trim(), line.substring(equalsOffset+1));
            }
        }
        return model;
    }

    public static Map<String, Object> loadEnvironmentInto(final Map<String, Object> context) {
        for (final Map.Entry<String, String> entry : System.getenv().entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }
        return context;
    }

}
