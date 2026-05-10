package dev.thatwhichis.rest.adapter.templates;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

// @formatter:off
public class GlobalTemplates {

    @CheckedTemplate(basePath = "error")
    public record error(String message) implements TemplateInstance {}

    @CheckedTemplate(basePath = "help")
    public record help(String text) implements TemplateInstance {}
}
// @formatter:on