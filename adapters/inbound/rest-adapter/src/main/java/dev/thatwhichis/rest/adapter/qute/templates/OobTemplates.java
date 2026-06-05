package dev.thatwhichis.rest.adapter.qute.templates;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

import java.util.Set;

@CheckedTemplate(basePath = "oob", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class OobTemplates {

    public static native TemplateInstance todosDeleteAll();

    public static native TemplateInstance todosDeleteById(Set<Integer> ids);
}
