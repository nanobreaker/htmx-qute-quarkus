package space.nanobreaker.configuration.microservice.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import space.nanobreaker.core.usecases.v1.todo.CreateTodoUseCase;

import java.time.LocalDate;

public class CreateTodoRequestTO {

    @FormParam("title")
    @NotBlank(message = "title can not be blank")
    private String title;
    @FormParam("description")
    private String description;

    @FormParam("target")
    private LocalDate target;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getTarget() {
        return target;
    }

    public void setTarget(LocalDate target) {
        this.target = target;
    }

    public CreateTodoUseCase.CreateTodoRequest createRequest() {
        return new CreateTodoUseCase.CreateTodoRequest(title, description, target);
    }
}

