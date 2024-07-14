package space.nanobreaker.configuration.monolith.extension;

import java.util.Objects;

public record Some<T>(T value) implements Option<T> {
    public Some {
        Objects.requireNonNull(value);
    }
}