package space.nanobreaker.library.option;

import java.util.Objects;

public record Some<T>(T value) implements Option<T> {
    public Some {
        Objects.requireNonNull(value);
    }
}