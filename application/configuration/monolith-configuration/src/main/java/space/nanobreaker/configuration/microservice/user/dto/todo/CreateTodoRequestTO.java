package space.nanobreaker.configuration.microservice.user.dto.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import lombok.Data;
import space.nanobreaker.core.usecases.v1.todo.command.CreateTodoCommand;

import java.time.LocalDate;

@Data
public class CreateTodoRequestTO {

    @FormParam("title")
    @NotBlank(message = "title can not be blank")
    private String title;

    @FormParam("description")
    private String description;

    @FormParam("target")
    private LocalDate target;

    public CreateTodoCommand mapToCreateTodoCommand() {
        return new CreateTodoCommand(title, description, target);
    }
}

