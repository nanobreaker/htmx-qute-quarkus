package space.nanobreaker.core.domain.v1;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Todo {

    @Id
    @GeneratedValue
    private UUID id;
    private String title;
    private String description;
    private LocalDate target;
    private Boolean completed;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public static final class TodoBuilder {
        private UUID id;
        private String title;
        private String description;
        private LocalDate target;
        private Boolean completed;

        private TodoBuilder() {
        }

        public static TodoBuilder aTodo() {
            return new TodoBuilder();
        }

        public TodoBuilder withId(UUID id) {
            this.id = id;
            return this;
        }

        public TodoBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public TodoBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public TodoBuilder withTarget(LocalDate target) {
            this.target = target;
            return this;
        }

        public TodoBuilder withCompleted(Boolean completed) {
            this.completed = completed;
            return this;
        }

        public Todo build() {
            Todo todo = new Todo();
            todo.setId(id);
            todo.setTitle(title);
            todo.setDescription(description);
            todo.setTarget(target);
            todo.setCompleted(completed);
            return todo;
        }
    }
}
