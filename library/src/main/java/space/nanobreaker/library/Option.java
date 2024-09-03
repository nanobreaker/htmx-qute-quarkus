package space.nanobreaker.library;

import java.util.Optional;
import java.util.function.Function;

public sealed interface Option<T> permits None, Some {

    static <T> Option<T> of(Optional<T> opt) {
        return opt.<Option<T>>map(Some::new).orElse(new None<>());
    }

    static <T> Option<T> of(T value) {
        return of(Optional.ofNullable(value));
    }

    static <T> Option<T> none() {
        return of(Optional.empty());
    }

    default boolean isSome(){
        return switch (this) {
            case Some<T> ignored -> true;
            case None<T> ignored -> false;
        };
    }

    default boolean isNone(){
        return switch (this) {
            case Some<T> ignored -> false;
            case None<T> ignored -> true;
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
}