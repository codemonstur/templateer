package templateer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SelectEngineTest {

    @TempDir
    Path tmp;

    @Test
    void autoDetectsStandardExtensions() {
        assertEquals(TemplateEngine.mustache, TemplateEngine.selectEngine(null, new File("t.mustache")));
        assertEquals(TemplateEngine.pebble, TemplateEngine.selectEngine(null, new File("t.pebble")));
        assertEquals(TemplateEngine.thymeleaf, TemplateEngine.selectEngine(null, new File("t.thymeleaf")));
        assertEquals(TemplateEngine.handlebars, TemplateEngine.selectEngine(null, new File("t.handlebars")));
    }

    @Test
    void autoDetectsAliasedExtensions() {
        assertEquals(TemplateEngine.freemarker, TemplateEngine.selectEngine(null, new File("t.ftl")));
        assertEquals(TemplateEngine.velocity, TemplateEngine.selectEngine(null, new File("t.vm")));
    }

    @Test
    void multiDotFilenameUsesLastExtension() {
        assertEquals(TemplateEngine.freemarker, TemplateEngine.selectEngine(null, new File("my.template.ftl")));
        assertEquals(TemplateEngine.velocity, TemplateEngine.selectEngine(null, new File("a.b.c.vm")));
    }

    @Test
    void unknownExtensionFallsBackToPebble() {
        assertEquals(TemplateEngine.pebble, TemplateEngine.selectEngine(null, new File("t.unknown")));
        assertEquals(TemplateEngine.pebble, TemplateEngine.selectEngine(null, new File("noextension")));
    }

    @Test
    void explicitTypeOverridesFile() {
        assertEquals(TemplateEngine.velocity, TemplateEngine.selectEngine(TemplateEngine.velocity, new File("t.ftl")));
        assertEquals(TemplateEngine.mustache, TemplateEngine.selectEngine(TemplateEngine.mustache, new File("t.ftl")));
    }

    @Test
    void pluginWritesNestedOutput() throws Exception {
        File input = tmp.resolve("in.vm").toFile();
        Files.writeString(input.toPath(), "Hello $name!");
        File output = tmp.resolve("nested/dir/out.txt").toFile();

        int result = invokePlugin(input, output, Map.of("name", "Nested"));

        assertEquals(0, result);
        assertTrue(output.exists());
        assertEquals("Hello Nested!", Files.readString(output.toPath()).trim());
    }

    private static int invokePlugin(File input, File output, Map<String, String> env) throws Exception {
        var method = BobPlugin.class.getDeclaredMethod("template",
            bobthebuildtool.pojos.buildfile.Project.class, Map.class, String[].class);
        method.setAccessible(true);
        return (int) method.invoke(null, null, env, new String[]{
            "-f", input.getAbsolutePath(), "-o", output.getAbsolutePath(), "-e"});
    }
}