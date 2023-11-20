package space.nanobreaker.configuration.microservice.user.resources;

import io.quarkus.qute.TemplateExtension;
import space.nanobreaker.core.domain.v1.Todo;

import java.time.LocalDate;
import java.time.Period;

@TemplateExtension(namespace = "todo")
public class TodoExtensions {

    public static String color(Todo todo) {
        final int days_left = Period.between(LocalDate.now(), todo.getTarget()).getDays();
        return switch (days_left) {
            case 0 -> "bad";
            case 1, 2 -> "warn";
            case 3, 4 -> "info";
            case 5, 6, 7, 9 -> "ok";
            default -> {
                if (days_left < 0) yield "bad";
                else if (days_left > 9) yield "";
                else yield "";
            }
        };
    }

}
