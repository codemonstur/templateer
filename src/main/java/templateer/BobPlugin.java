package templateer;

import bobthebuildtool.pojos.buildfile.Project;
import bobthebuildtool.pojos.error.VersionTooOld;

import java.util.Map;

import static bobthebuildtool.services.Update.requireBobVersion;

public enum BobPlugin {;

    private static final String DESCRIPTION_TEMPLATE = "Applies a set of variables to a template, writes the result";

    public static void installPlugin(final Project project) throws VersionTooOld {
        requireBobVersion("5");
        project.addCommand("template", DESCRIPTION_TEMPLATE, BobPlugin::template);
    }

    private static int template(final Project project, final Map<String, String> environment, final String[] args) {
        Main.main(args);
        return 0;
    }

}
