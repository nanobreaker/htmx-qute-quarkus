package space.nanobreaker.library.result;

public record Ok<V, E>(V value) implements Result<V, E> {

}