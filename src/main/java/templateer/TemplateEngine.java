package templateer;

import com.github.jknack.handlebars.Handlebars;
import com.hubspot.jinjava.Jinjava;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import de.neuland.jade4j.Jade4J;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.thymeleaf.context.IContext;
import org.trimou.engine.MustacheEngineBuilder;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static templateer.Functions.toExtension;

public enum TemplateEngine {
    handlebars() {
        public String processTemplate(final File file, final Map<String, Object> model) throws IOException {
            return new Handlebars().compile(file.getAbsolutePath()).apply(model);
        }
    },
    jade() {
        public String processTemplate(final File file, final Map<String, Object> model) throws IOException {
            return Jade4J.render(file.getAbsolutePath(), model);
        }
    },
    jinjava() {
        public String processTemplate(final File file, final Map<String, Object> model) throws IOException {
            return new Jinjava().render(Files.readString(file.toPath()), model);
        }
    },
    jtwig() {
        public String processTemplate(final File file, final Map<String, Object> model) {
            return JtwigTemplate.fileTemplate(file).render(JtwigModel.newModel(model));
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
            } catch (PebbleException e) {
                throw new IOException(e);
            }
        }
    },
    thymeleaf() {
        public String processTemplate(final File file, final Map<String, Object> model) throws IOException {
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
    };

    public abstract String processTemplate(final File file, final Map<String, Object> model) throws IOException;

    public static TemplateEngine selectEngine(final TemplateEngine type, final File inputFile) {
        if (type != null) return type;
        try {
            return TemplateEngine.valueOf(toExtension(inputFile));
        } catch (Exception e) {
            return pebble;
        }
    }

}
