package templateer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SelectEngineTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void autoDetectsStandardExtensions() {
        assertEquals(TemplateEngine.mustache, TemplateEngine.selectEngine(null, new File("t.mustache")));
        assertEquals(TemplateEngine.pebble, TemplateEngine.selectEngine(null, new File("t.pebble")));
        assertEquals(TemplateEngine.thymeleaf, TemplateEngine.selectEngine(null, new File("t.thymeleaf")));
        assertEquals(TemplateEngine.handlebars, TemplateEngine.selectEngine(null, new File("t.handlebars")));
    }

    @Test
    public void autoDetectsAliasedExtensions() {
        assertEquals(TemplateEngine.freemarker, TemplateEngine.selectEngine(null, new File("t.ftl")));
        assertEquals(TemplateEngine.velocity, TemplateEngine.selectEngine(null, new File("t.vm")));
    }

    @Test
    public void multiDotFilenameUsesLastExtension() {
        assertEquals(TemplateEngine.freemarker, TemplateEngine.selectEngine(null, new File("my.template.ftl")));
        assertEquals(TemplateEngine.velocity, TemplateEngine.selectEngine(null, new File("a.b.c.vm")));
    }

    @Test
    public void unknownExtensionFallsBackToPebble() {
        assertEquals(TemplateEngine.pebble, TemplateEngine.selectEngine(null, new File("t.unknown")));
        assertEquals(TemplateEngine.pebble, TemplateEngine.selectEngine(null, new File("noextension")));
    }

    @Test
    public void explicitTypeOverridesFile() {
        assertEquals(TemplateEngine.velocity, TemplateEngine.selectEngine(TemplateEngine.velocity, new File("t.ftl")));
        assertEquals(TemplateEngine.mustache, TemplateEngine.selectEngine(TemplateEngine.mustache, new File("t.ftl")));
    }

    @Test
    public void pluginWritesNestedOutput() throws Exception {
        File input = tmp.newFile("in.vm");
        Files.writeString(input.toPath(), "Hello $name!");
        File output = new File(tmp.getRoot(), "nested/dir/out.txt");

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
