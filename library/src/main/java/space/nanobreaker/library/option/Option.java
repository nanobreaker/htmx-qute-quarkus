package space.nanobreaker.library.option;

import space.nanobreaker.library.error.Error;
import space.nanobreaker.library.result.Result;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Option<T> permits None, Some {

    static <T> Option<T> of(
            final Optional<T> opt
    ) {
        return opt.<Option<T>>map(Some::new).orElse(new None<>());
    }

    static <T> Option<T> of(
            final T value
    ) {
        return of(Optional.ofNullable(value));
    }

    static <T> Option<T> none() {
        return of(Optional.empty());
    }

    default boolean isSome() {
        return switch (this) {
            case Some<T> ignored -> true;
            case None<T> ignored -> false;
        };
    }

    default boolean isNone() {
        return switch (this) {
            case Some<T> ignored -> false;
            case None<T> ignored -> true;
        };
    }

    default Result<T, Error> okOr(final Error error) {
        return switch (this) {
            case Some(T v) -> Result.ok(v);
            case None() -> Result.err(error);
        };
    }

    default <NV> Option<NV> map(
            final Function<? super T, ? extends NV> valueMapper
    ) {
        return switch (this) {
            case Some(T v) -> Option.of(valueMapper.apply(v));
            case None() -> Option.none();
        };
    }

    default <NV> Option<NV> flatMap(
            final Function<? super T, ? extends Option<NV>> valueMapper
    ) {
        return switch (this) {
            case Some(T v) -> valueMapper.apply(v);
            case None() -> Option.none();
        };
    }

    default T value() {
        return switch (this) {
            case Some(T t) -> t;
            case None<T> ignored -> null;
        };
    }

    default T orNull() {
        return switch (this) {
            case Some(T t) -> t;
            case None<T> ignored -> null;
        };
    }

    default T orElse(
            final T other
    ) {
        return switch (this) {
            case Some(T t) -> t;
            case None<T> ignored -> other;
        };
    }

    default T orElseGet(
            final Supplier<? extends T> supplier
    ) {
        return switch (this) {
            case Some(T t) -> t;
            case None<T> ignored -> supplier.get();
        };
    }

    default Result<T, Error> orElseResult(
            final Error error
    ) {
        return switch (this) {
            case Some(T t) -> Result.ok(t);
            case None<T> ignored -> Result.err(error);
        };
    }

    default T orElseThrow() {
        return switch (this) {
            case Some(T t) -> t;
            case None<T> ignored -> throw new NoSuchElementException("No value present");
        };
    }
}