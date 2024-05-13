package space.nanobreaker.configuration.monolith.resources.todo;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import space.nanobreaker.core.domain.v1.Todo;

import java.util.List;

@CheckedTemplate(basePath = "todo", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class TodoTemplates {

    static native TemplateInstance todoBoard(List<Todo> todos);

    static native TemplateInstance todoBoard$todo(Todo todo);

}
