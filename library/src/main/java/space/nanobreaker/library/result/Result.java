package space.nanobreaker.library.result;

import space.nanobreaker.library.option.Option;
import space.nanobreaker.library.tuple.Tuple;

import java.util.function.Function;

public sealed interface Result<V, E>
        permits Ok, Err {

    static <E> Result<Void, E> okVoid() {
        return new Ok<>(null);
    }

    static <V, E> Result<V, E> ok(final V value) {
        return new Ok<>(value);
    }

    static <V, E> Result<V, E> err(final E error) {
        return new Err<>(error);
    }

    default boolean isOk() {
        return this instanceof Ok;
    }

    default boolean isErr() {
        return this instanceof Err;
    }

    default Option<V> ok() {
        return switch (this) {
            case Ok(V v) -> Option.of(v);
            case Err(E ignored) -> Option.none();
        };
    }

    default Option<E> err() {
        return switch (this) {
            case Ok(V ignored) -> Option.none();
            case Err(E e) -> Option.of(e);
        };
    }

    default V unwrap() {
        return switch (this) {
            case Ok(V v) -> v;
            case Err(E ignored) -> throw new IllegalStateException("Err can not be unwrapped");
        };
    }

    default E error() {
        return switch (this) {
            case Ok(V _) -> throw new IllegalStateException("Ok can not be Err");
            case Err(E e) -> e;
        };
    }

    default V orThrow() {
        return switch (this) {
            case Ok(V v) -> v;
            case Err(E e) -> throw new IllegalArgumentException(e.toString());
        };
    }

    default <NV> Result<NV, E> map(
            final Function<? super V, ? extends NV> valueMapper
    ) {
        return switch (this) {
            case Ok(V v) -> Result.ok(valueMapper.apply(v));
            case Err(E e) -> Result.err(e);
        };
    }

    default <NE> Result<V, NE> mapErr(
            final Function<? super E, ? extends NE> valueMapper
    ) {
        return switch (this) {
            case Ok(V v) -> Result.ok(v);
            case Err(E e) -> Result.err(valueMapper.apply(e));
        };
    }

    default <NV> Result<NV, E> flatMap(
            final Function<? super V, ? extends Result<NV, E>> valueMapper
    ) {
        return switch (this) {
            case Ok(V v) -> valueMapper.apply(v);
            case Err(E e) -> Result.err(e);
        };
    }

    static <F, S, E> Result<Tuple<F, S>, E> merge(
            final Result<F, E> first,
            final Result<S, E> second
    ) {
        final var tuple = new Tuple<>(first, second);
        return switch (tuple) {
            case Tuple(Ok(F f), Ok(S s)) -> Result.ok(new Tuple<F, S>(f, s));
            case Tuple(Ok(F ignored), Err(E e)) -> Result.err(e);
            case Tuple(Err(E e), Ok(S ignored)) -> Result.err(e);
            case Tuple(Err(E e), Err(E ignored)) -> Result.err(e);
        };
    }

    // todo: is there a way to implement flatten without unchecked types ?
    default <IV> Result<IV, E> flatten() {
        switch (this) {
            case Ok<V, E> v -> {
                @SuppressWarnings("unchecked")
                Result<IV, E> value = (Result<IV, E>) v.value();
                return value;
            }
            case Err<V, E> v -> {
                return Result.err(v.error());
            }
        }
    }

    // todo: is there a way to implement flat based on 'this' ?
    static <IV, E> Result<IV, E> flat(final Result<Result<IV, E>, E> nestedResult) {
        switch (nestedResult) {
            case Ok<Result<IV, E>, E> v -> {
                return v.value();
            }
            case Err<Result<IV, E>, E> v -> {
                final E error = v.error();
                return Result.err(error);
            }
        }
    }
}