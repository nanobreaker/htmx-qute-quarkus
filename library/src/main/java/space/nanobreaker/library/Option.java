package space.nanobreaker.library;

import java.util.Optional;

public sealed interface Option<T> permits None, Some {

    static <T> Option<T> over(Optional<T> opt) {
        return opt.<Option<T>>map(Some::new).orElse(new None<>());
    }

    static <T> Option<T> over(T value) {
        return over(Optional.ofNullable(value));
    }

    static <T> Option<T> none() {
        return over(Optional.empty());
    }

    default T value() {
        return switch (this) {
            case Some(T t) -> t;
            case None<T> ignored -> null;
        };
    }
}