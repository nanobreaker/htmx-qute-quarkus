package space.nanobreaker.configuration.monolith.templates;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate(basePath = "topbar", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class TopbarTemplates {

    public static native TemplateInstance topbar(
            String applicationName,
            String applicationVersion,
            String username
    );
}
