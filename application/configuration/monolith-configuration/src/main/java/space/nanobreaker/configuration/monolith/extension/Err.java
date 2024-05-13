package space.nanobreaker.configuration.monolith.extension;

public record Err<T, E>(E error) implements Result<T, E> {
}
