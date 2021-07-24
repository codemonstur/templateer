package templateer;

import jcli.errors.InvalidCommandLine;
import templateer.model.Arguments;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.file.Files.writeString;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static jcli.CliParser.parseCommandLineArguments;
import static templateer.core.IO.loadModel;
import static templateer.core.TemplateEngine.selectEngine;

public enum Main {;

    public static void main(final String... args) {
        try {
            final var arguments = parseCommandLineArguments(args, Arguments::new);
            final var model = loadModel(arguments);
            final var inFile = new File(arguments.inputFile);
            final var outFile = Paths.get(arguments.outputFile);
            final var engine = selectEngine(arguments.type, inFile);

            writeString(outFile, engine.processTemplate(inFile, model), CREATE_NEW);

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
