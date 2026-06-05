package dev.thatwhichis.rest.adapter.qute.templates;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate(basePath = "help")
public class HelpTemplates {

    public static native TemplateInstance help(String text);
}
