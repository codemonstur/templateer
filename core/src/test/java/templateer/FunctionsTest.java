package templateer;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionsTest {

    @Test
    void toExtensionReturnsLastSegmentAfterFinalDot() {
        assertEquals("ftl", Functions.toExtension(new File("my.template.ftl")));
        assertEquals("vm", Functions.toExtension(new File("a.b.c.vm")));
        assertEquals("properties", Functions.toExtension(new File("config.properties")));
        assertEquals("", Functions.toExtension(new File("noextension")));
    }

    @Test
    void loadModelReadsUtf8Properties() throws Exception {
        File f = File.createTempFile("model", ".properties");
        f.deleteOnExit();
        Files.writeString(f.toPath(), "name=Caf\u00e9\n");

        Map<String, Object> model = Functions.loadModel(f.getAbsolutePath(), false, Map.of());

        assertEquals("Caf\u00e9", model.get("name"));
    }

    @Test
    void loadModelEnvAndFileMerge() throws Exception {
        File f = File.createTempFile("model", ".properties");
        f.deleteOnExit();
        Files.writeString(f.toPath(), "fromFile=yes\n");

        Map<String, Object> model = Functions.loadModel(f.getAbsolutePath(), true, Map.of("fromEnv", "yes"));

        assertEquals("yes", model.get("fromFile"));
        assertEquals("yes", model.get("fromEnv"));
    }
}