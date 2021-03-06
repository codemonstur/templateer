package templateer;

import com.github.jknack.handlebars.Handlebars;
import com.hubspot.jinjava.Jinjava;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import de.neuland.jade4j.Jade4J;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.thymeleaf.context.IContext;
import org.trimou.engine.MustacheEngineBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public interface TemplateEngine {

    Map<String, TemplateEngine> ENGINES = ofEntries(
        entry(".handlebars", TemplateEngine::applyTemplateHandlebars),
        entry(".jade", TemplateEngine::applyTemplateJade4J),
        entry(".jinjava", TemplateEngine::applyTemplateJinJava),
        entry(".jtwig", TemplateEngine::applyTemplateJTwig),
        entry(".md", TemplateEngine::applyTemplateMarkdown),
        entry(".mustache", TemplateEngine::applyTemplateMustache),
        entry(".pebble", TemplateEngine::applyTemplatePebble),
        entry(".thymeleaf", TemplateEngine::applyTemplateThymeleaf));

    String processTemplate(File file, Map<String, Object> model) throws IOException;


    public static String applyTemplateHandlebars(final File file, final Map<String, Object> model) throws IOException {
        return new Handlebars().compile(file.getAbsolutePath()).apply(model);
    }
    public static String applyTemplateJTwig(final File file, final Map<String, Object> model) {
        return JtwigTemplate.fileTemplate(file).render(JtwigModel.newModel(model));
    }
    public static String applyTemplateJade4J(final File file, final Map<String, Object> model) throws IOException {
        return Jade4J.render(file.getAbsolutePath(), model);
    }
    public static String applyTemplateJinJava(final File file, final Map<String, Object> model) throws IOException {
        return new Jinjava().render(Files.readString(file.toPath()), model);
    }
    public static String applyTemplateMarkdown(final File file, final Map<String, Object> model) throws IOException {
        final MutableDataSet options = new MutableDataSet();
        try (final var reader = new FileReader(file)) {
            return HtmlRenderer.builder(options).build()
                    .render(Parser.builder(options).build().parseReader(reader));
        }
    }
    public static String applyTemplateMustache(final File file, final Map<String, Object> model) throws IOException {
        return MustacheEngineBuilder.newBuilder().build()
            .compileMustache(Files.readString(file.toPath())).render(model);
    }
    public static String applyTemplatePebble(final File file, final Map<String, Object> model) throws IOException {
        try {
            final PebbleTemplate template = new PebbleEngine.Builder().build().getTemplate(file.getAbsolutePath());
            try (final StringWriter writer = new StringWriter()) {
                template.evaluate(writer, model);
                return writer.toString();
            }
        } catch (PebbleException e) {
            throw new IOException(e);
        }
    }
    public static String applyTemplateThymeleaf(final File file, final Map<String, Object> model) throws IOException {
        final IContext context = new IContext() {
            public Locale getLocale() {
                return Locale.getDefault();
            }
            public boolean containsVariable(String s) {
                return model.containsKey(s);
            }
            public Set<String> getVariableNames() {
                return model.keySet();
            }
            public Object getVariable(String s) {
                return model.get(s);
            }
        };
        return new org.thymeleaf.TemplateEngine().process(Files.readString(file.toPath()), context);
    }
}
