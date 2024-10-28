package space.nanobreaker.configuration.monolith.templates;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import space.nanobreaker.core.domain.v1.todo.Todo;

import java.util.Set;

@CheckedTemplate(basePath = "todos", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class TodoTemplates {

    public static native TemplateInstance todo(Todo todo);

    public static native TemplateInstance todoCreate();

    public static native TemplateInstance todos(Set<Todo> todos);

    public static native TemplateInstance todosDelete(Set<Integer> ids);
}