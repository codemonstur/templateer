package templateer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public enum Functions {;

    public static String toExtension(final File inFile) {
        final String name = inFile.getName();
        final int index = name.lastIndexOf('.');
        return index == -1 ? "" : name.substring(index + 1);
    }

    public static Map<String, Object> loadModel(final String modelFile, final boolean loadEnv,
                                               final Map<String, String> environment) throws IOException {
        final var model = new HashMap<String, Object>();
        if (loadEnv) model.putAll(environment);
        if (!isNullOrEmpty(modelFile)) {
            final Properties props = new Properties();
            try (final Reader reader = new InputStreamReader(new FileInputStream(modelFile), StandardCharsets.UTF_8)) {
                props.load(reader);
            }
            props.forEach((o, o2) -> model.put(o.toString(), o2.toString()));
        }
        return model;
    }

    public static boolean isNullOrEmpty(final String value) {
        return value == null || value.isEmpty();
    }

}
