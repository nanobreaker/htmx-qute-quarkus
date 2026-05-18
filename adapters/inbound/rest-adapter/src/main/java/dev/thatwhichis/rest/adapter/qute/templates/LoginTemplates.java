package dev.thatwhichis.rest.adapter.qute.templates;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate(basePath = "login", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class LoginTemplates {

    public static native TemplateInstance login();
}
