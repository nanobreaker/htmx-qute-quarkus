package space.nanobreaker.configuration.monolith.dto.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.Context;
import space.nanobreaker.core.usecases.v1.todo.command.TodoCreateCommand;

import java.security.Principal;
import java.time.LocalDateTime;

public class CreateTodoRequestTO {

    @Context
    private Principal principal;

    @FormParam("title")
    @NotBlank(message = "title can not be blank")
    private String title;

    @FormParam("description")
    private String description;

    @FormParam("start")
    private LocalDateTime start;

    @FormParam("end")
    private LocalDateTime end;

    public TodoCreateCommand mapToCommand() {
        return new TodoCreateCommand(principal.getName(), "test", description, start, end);
    }
}

