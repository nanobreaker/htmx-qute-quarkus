package space.nanobreaker.core.domain.v1;

import java.time.LocalDate;
import java.util.UUID;

public record ReadTodo(
        UUID id,
        String title,
        String description,
        LocalDate target
) {

}