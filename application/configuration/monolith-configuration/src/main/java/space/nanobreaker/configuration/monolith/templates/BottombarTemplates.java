package space.nanobreaker.configuration.monolith.templates;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate(basePath = "bottombar", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class BottombarTemplates {

    public static native TemplateInstance bottombar();
}
