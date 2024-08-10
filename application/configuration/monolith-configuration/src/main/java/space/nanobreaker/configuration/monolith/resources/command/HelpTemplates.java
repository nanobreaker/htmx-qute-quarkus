package space.nanobreaker.configuration.monolith.resources.command;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate(basePath = "help", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class HelpTemplates {
    public static native TemplateInstance help(String text);

}
