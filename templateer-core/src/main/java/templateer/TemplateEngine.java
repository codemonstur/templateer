package templateer;

import com.github.jknack.handlebars.Handlebars;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.trimou.engine.MustacheEngineBuilder;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

import static templateer.Functions.toExtension;

public enum TemplateEngine {
    handlebars() {
        public String processTemplate(final File file, final Map<String, Object> model) throws IOException {
            return new Handlebars().compile(file.getAbsolutePath()).apply(model);
        }
    },
    mustache() {
        public String processTemplate(final File file, final Map<String, Object> model) throws IOException {
            return MustacheEngineBuilder.newBuilder().build()
                .compileMustache(Files.readString(file.toPath())).render(model);
        }
    },
    pebble() {
        public String processTemplate(final File file, final Map<String, Object> model) throws IOException {
            try {
                final PebbleTemplate template = new PebbleEngine.Builder().autoEscaping(false).newLineTrimming(false)
                        .build().getTemplate(file.getAbsolutePath());
                try (final StringWriter writer = new StringWriter()) {
                    template.evaluate(writer, model);
                    return writer.toString();
                }
            } catch (final PebbleException e) {
                throw new IOException(e);
            }
        }
    },
    freemarker() {
        public String processTemplate(final File file, final Map<String, Object> model) throws IOException {
            final Configuration configuration = new Configuration(Configuration.VERSION_2_3_34);
            configuration.setDefaultEncoding("UTF-8");
            final File absolute = file.getAbsoluteFile();
            configuration.setDirectoryForTemplateLoading(absolute.getParentFile());
            final Template template = configuration.getTemplate(absolute.getName());
            try (final StringWriter writer = new StringWriter()) {
                template.process(model, writer);
                return writer.toString();
            } catch (final TemplateException e) {
                throw new IOException(e);
            }
        }
    },
    velocity() {
        public String processTemplate(final File file, final Map<String, Object> model) throws IOException {
            final File absolute = file.getAbsoluteFile();
            final Properties properties = new Properties();
            properties.setProperty("resource.loader", "file");
            properties.setProperty("file.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
            properties.setProperty("file.resource.loader.path", absolute.getParent());
            final VelocityEngine velocityEngine = new VelocityEngine(properties);
            final VelocityContext context = new VelocityContext();
            model.forEach(context::put);
            try (final StringWriter writer = new StringWriter()) {
                velocityEngine.getTemplate(absolute.getName(), "UTF-8").merge(context, writer);
                return writer.toString();
            }
        }
    };

    public abstract String processTemplate(final File file, final Map<String, Object> model) throws IOException;

    private static final Map<String, TemplateEngine> EXTENSION_ALIASES = Map.of(
        "ftl", freemarker,
        "vm", velocity
    );

    public static TemplateEngine selectEngine(final TemplateEngine type, final File inputFile) {
        if (type != null) return type;
        final String extension = toExtension(inputFile);
        try {
            return TemplateEngine.valueOf(extension);
        } catch (final Exception e) {
            return EXTENSION_ALIASES.getOrDefault(extension, pebble);
        }
    }

}
