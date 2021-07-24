package templateer.model;

import jcli.annotations.CliCommand;
import jcli.annotations.CliOption;
import templateer.core.TemplateEngine;

@CliCommand(name = "templateer", description = "Command line tool to apply a set of variables to a template")
public class Arguments {
    @CliOption(name = 'f', longName = "inputFile", isMandatory = true)
    public String inputFile;
    @CliOption(name = 'o', longName = "outputFile", isMandatory = true)
    public String outputFile;
    @CliOption(name = 'm', longName = "model")
    public String modelFile;
    @CliOption(name = 'e', longName = "env")
    public boolean loadEnv;
    @CliOption(name = 't', longName = "type")
    public TemplateEngine type;
}