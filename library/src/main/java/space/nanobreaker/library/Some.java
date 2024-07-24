package space.nanobreaker.library;

import java.util.Objects;

public record Some<T>(T value) implements Option<T> {
    public Some {
        Objects.requireNonNull(value);
    }
}