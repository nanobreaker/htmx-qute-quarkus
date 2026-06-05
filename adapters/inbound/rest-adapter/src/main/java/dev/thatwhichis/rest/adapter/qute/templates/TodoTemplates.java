package dev.thatwhichis.rest.adapter.qute.templates;

import dev.thatwhichis.core.domain.todo.Todo;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

import java.time.ZoneId;
import java.util.Set;

@CheckedTemplate(basePath = "todo", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class TodoTemplates {

    public static native TemplateInstance todos(Set<Todo> todos, ZoneId zone);

    public static native TemplateInstance todos$item(Todo todo, ZoneId zone);

    public static native TemplateInstance todos$items(Set<Todo> todos, ZoneId zone);
}



