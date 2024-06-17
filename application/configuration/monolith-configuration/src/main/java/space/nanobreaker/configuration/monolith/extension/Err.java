package space.nanobreaker.configuration.monolith.extension;

public record Err<V, E>(E error) implements Result<V, E> {

}
