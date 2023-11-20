package space.nanobreaker.configuration.microservice.user.resources;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import space.nanobreaker.core.domain.v1.Todo;

import java.util.List;

@CheckedTemplate(basePath = "v1/todo", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class TodoTemplates {

    static native TemplateInstance todoForm();

    static native TemplateInstance todos(List<Todo> todos);

    static native TemplateInstance todos$todo(Todo todo);

}
