package space.nanobreaker.library.result;

public record Err<V, E>(E error) implements Result<V, E> {

}
