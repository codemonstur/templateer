package templateer;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import jcli.errors.InvalidCommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.writeString;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static jcli.CliParser.parseCommandLineArguments;
import static templateer.Functions.loadModel;
import static templateer.TemplateEngine.selectEngine;

public final class Main {;

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

    public static void main(final String[] args) {
        try {
            final var arguments = parseCommandLineArguments(args, Arguments::new);
            final var model = loadModel(arguments.modelFile, arguments.loadEnv, System.getenv());
            final var engine = selectEngine(arguments.type, arguments.inputFile);

            final var output = engine.processTemplate(arguments.inputFile, model);
            final var parent = arguments.outputFile.toAbsolutePath().getParent();
            if (parent != null) createDirectories(parent);
            writeString(arguments.outputFile, output, CREATE, WRITE, TRUNCATE_EXISTING);
        } catch (final InvalidCommandLine e) {
            System.err.println(e.getMessage());
            System.exit(2);
        } catch (final IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}