package space.nanobreaker.configuration.monolith.extension;

public record Ok<V, E>(V value) implements Result<V, E> {

}