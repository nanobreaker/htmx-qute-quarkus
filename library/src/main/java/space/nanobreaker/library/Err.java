package space.nanobreaker.library;

public record Err<V, E>(E error) implements Result<V, E> {

}
