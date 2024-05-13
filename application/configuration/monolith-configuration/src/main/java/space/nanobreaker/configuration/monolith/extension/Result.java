package space.nanobreaker.configuration.monolith.extension;

public sealed interface Result<T, E>
        permits Ok, Err {

    static <T, E> Result<T, E> ok(T value) {
        return new Ok<>(value);
    }

    static <T, E> Result<T, E> err(E error) {
        return new Err<>(error);
    }
}