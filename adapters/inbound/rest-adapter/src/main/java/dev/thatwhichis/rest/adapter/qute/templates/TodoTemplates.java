package dev.thatwhichis.rest.adapter.qute.templates;

import dev.thatwhichis.core.domain.todo.Todo;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

import java.util.Set;

@CheckedTemplate(basePath = "todo", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class TodoTemplates {

    public static native TemplateInstance todos(Set<Todo> todos);

    public static native TemplateInstance todos$item(Todo todo);

    public static native TemplateInstance todos$items(Set<Todo> todos);
}



