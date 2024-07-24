package space.nanobreaker.library;

import java.util.Optional;

public sealed interface Option<T> permits None, Some {
    static <T> Option<T> over(Optional<T> opt) {
        return opt.<Option<T>>map(Some::new).orElse(new None<>());
    }
}