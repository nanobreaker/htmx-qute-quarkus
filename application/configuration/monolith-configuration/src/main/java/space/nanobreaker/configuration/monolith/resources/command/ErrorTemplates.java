package space.nanobreaker.configuration.monolith.resources.command;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate(basePath = "error", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class ErrorTemplates {
    public static native TemplateInstance error(String error);

}
