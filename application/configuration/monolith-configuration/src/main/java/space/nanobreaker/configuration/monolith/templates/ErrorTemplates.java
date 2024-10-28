package space.nanobreaker.configuration.monolith.templates;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate(basePath = "error")
public class ErrorTemplates {

    public static native TemplateInstance error(
            String message
    );
}
