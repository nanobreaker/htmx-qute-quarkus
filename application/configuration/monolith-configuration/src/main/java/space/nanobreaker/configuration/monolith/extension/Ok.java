package space.nanobreaker.configuration.monolith.extension;

public record Ok<T, E>(T result) implements Result<T, E> {
}