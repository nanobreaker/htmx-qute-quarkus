package space.nanobreaker.configuration.monolith.extension;

import java.util.function.Function;

public sealed interface Result<V, E>
        permits Ok, Err {

    static <V, E> Result<V, E> ok(V value) {

        return new Ok<>(value);
    }

    static <V, E> Result<V, E> err(E error) {
        return new Err<>(error);
    }

    default boolean isOk() {
        return this instanceof Ok;
    }

    default boolean isErr() {
        return this instanceof Err;
    }

    default V unwrap() {
        return switch (this) {
            case Ok(V v) -> v;
            case Err(_) -> throw new IllegalStateException("Err can not be unwrapped");
        };
    }

    default <NV> Result<NV, E> map(Function<? super V, ? extends NV> valueMapper) {
        return switch (this) {
            case Ok(V v) -> Result.ok(valueMapper.apply(v));
            case Err(E e) -> Result.err(e);
        };
    }

    default <NV> Result<NV, E> flatMap(Function<? super V, ? extends Result<NV, E>> valueMapper) {
        return switch (this) {
            case Ok(V v) -> valueMapper.apply(v);
            case Err(E e) -> Result.err(e);
        };
    }

    // Result<Result<IV, E>, E>
    //        V = Result<IV, E>
    default Result<V, E> flatten() {
        throw new IllegalStateException("not implemented");
    }

    static <F, S, E> Result<Tuple<F, S>, E> merge(Result<F, E> first, Result<S, E> second) {
        final var tuple = new Tuple<>(first, second);
        return switch (tuple) {
            case Tuple(Ok(F f), Ok(S s)) -> Result.ok(new Tuple<F, S>(f, s));
            case Tuple(Ok(F _), Err(E e)) -> Result.err(e);
            case Tuple(Err(E e), Ok(S _)) -> Result.err(e);
            case Tuple(Err(E e), Err(E _)) -> Result.err(e);
        };
    }

}