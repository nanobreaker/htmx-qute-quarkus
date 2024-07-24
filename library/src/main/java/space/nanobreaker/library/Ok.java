package space.nanobreaker.library;

public record Ok<V, E>(V value) implements Result<V, E> {

}