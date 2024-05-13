package space.nanobreaker.configuration.monolith.dto.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.Context;
import lombok.Data;
import space.nanobreaker.core.usecases.v1.todo.command.CreateTodoCommand;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
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

    public CreateTodoCommand mapToCommand() {
        return new CreateTodoCommand(principal.getName(), description, start, end);
    }
}

