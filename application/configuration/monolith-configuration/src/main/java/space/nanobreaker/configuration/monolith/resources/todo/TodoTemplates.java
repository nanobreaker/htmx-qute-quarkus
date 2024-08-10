package space.nanobreaker.configuration.monolith.resources.todo;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import space.nanobreaker.core.domain.v1.todo.Todo;

@CheckedTemplate(basePath = "todo", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class TodoTemplates {

    public static native TemplateInstance todo(Todo todo, boolean swap);

}
