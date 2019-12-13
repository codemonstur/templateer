package templateer;

import jcli.annotations.CliOption;
import jcli.errors.InvalidCommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static templateer.IO.loadModel;
import static templateer.IO.toExtension;
import static templateer.TemplateEngine.ENGINES;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static jcli.CliParser.parseCommandLineArguments;

public enum Main {;

    public static class Arguments {
        @CliOption(name = 'i', isMandatory = true)
        private String inputFile;
        @CliOption(name = 'o', isMandatory = true)
        private String outputFile;
        @CliOption(name = 'm', isMandatory = true)
        private String modelFile;
    }

    public static void main(final String... args) {
        try {
            final var arguments = parseCommandLineArguments(args, Arguments::new);
            final var model = loadModel(arguments.modelFile);
            final File inFile = new File(arguments.inputFile);
            final Path outFile = Paths.get(arguments.outputFile);

            final TemplateEngine engine = ENGINES.get(toExtension(inFile));
            if (engine == null) throw new IllegalArgumentException("No available template engine for file " + inFile.getName());

            final String output = engine.processTemplate(inFile, model);
            Files.writeString(outFile, output, CREATE_NEW);

        } catch (InvalidCommandLine exp) {
            System.err.println("Invalid command line options. Reason: " + exp.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(3);
        }
    }

}
