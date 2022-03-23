package templateer;

import bobthebuildtool.pojos.buildfile.Project;
import bobthebuildtool.pojos.error.VersionTooOld;
import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import jcli.errors.InvalidCommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static bobthebuildtool.services.Update.requireBobVersion;
import static java.nio.file.Files.writeString;
import static java.nio.file.StandardOpenOption.*;
import static jcli.CliParser.parseCommandLineArguments;
import static templateer.Functions.loadModel;
import static templateer.TemplateEngine.selectEngine;

public enum BobPlugin {;

    private static final String DESCRIPTION_TEMPLATE = "Applies a set of variables to a template, writes the result";

    public static void installPlugin(final Project project) throws VersionTooOld {
        requireBobVersion("7");
        project.addCommand("template", DESCRIPTION_TEMPLATE, BobPlugin::template);
    }

    @CliCommand(name = "templateer", description = "Command line tool to apply a set of variables to a template")
    public static class Arguments {
        @CliOption(name = 'f', longName = "inputFile", isMandatory = true, description = "The template file")
        public File inputFile;
        @CliOption(name = 'o', longName = "outputFile", isMandatory = true, description = "The output file")
        public Path outputFile;
        @CliOption(name = 'm', longName = "model", description = "The properties file to use")
        public String modelFile;
        @CliOption(name = 'e', longName = "env", description = "If set loads the environment variables, if model is also set the environment variables will be overwritten")
        public boolean loadEnv;
        @CliOption(name = 't', longName = "type")
        public TemplateEngine type;
    }

    private static int template(final Project project, final Map<String, String> environment, final String[] args)
            throws InvalidCommandLine, IOException {
        final var arguments = parseCommandLineArguments(args, Arguments::new);
        final var model = loadModel(arguments, environment);
        final var engine = selectEngine(arguments.type, arguments.inputFile);

        final var output = engine.processTemplate(arguments.inputFile, model);
        writeString(arguments.outputFile, output, CREATE, WRITE, TRUNCATE_EXISTING);

        return 0;
    }

}
