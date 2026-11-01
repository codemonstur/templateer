package templateer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateEngineTest {

    @TempDir
    Path tmp;

    @Test
    void mustacheProcessesTemplate() throws Exception {
        Path p = tmp.resolve("t.mustache");
        Files.writeString(p, "Hello {{name}}!");
        String out = TemplateEngine.mustache.processTemplate(p.toFile(), Map.of("name", "World"));
        assertEquals("Hello World!", out);
    }

    @Test
    void freemarkerProcessesTemplate() throws Exception {
        Path p = tmp.resolve("t.ftl");
        Files.writeString(p, "Hello ${name}!");
        String out = TemplateEngine.freemarker.processTemplate(p.toFile(), Map.of("name", "World"));
        assertEquals("Hello World!", out);
    }

    @Test
    void velocityProcessesTemplate() throws Exception {
        Path p = tmp.resolve("t.vm");
        Files.writeString(p, "Hello $name!");
        String out = TemplateEngine.velocity.processTemplate(p.toFile(), Map.of("name", "World"));
        assertTrue(out.contains("Hello") && out.contains("World"));
    }

}
