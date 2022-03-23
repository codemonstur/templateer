package templateer;

import templateer.BobPlugin.Arguments;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public enum Functions {;

    public static String toExtension(final File inFile) {
        final String name = inFile.getName();
        final int index = name.indexOf('.');
        return index == -1 ? "" : name.substring(index);
    }

    public static Map<String, Object> loadModel(final Arguments arguments, final Map<String, String> environment) throws IOException {
        final var model = new HashMap<String, Object>();
        if (arguments.loadEnv) model.putAll(environment);
        if (!isNullOrEmpty(arguments.modelFile)) {
            final Properties props = new Properties();
            try (final Reader reader = new FileReader(arguments.modelFile)) {
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
