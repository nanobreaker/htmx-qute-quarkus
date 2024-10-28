package space.nanobreaker.library.tuple;

public record Tuple<F, S>(F first, S second) {

    public static <F, S> Tuple<F, S> of(
            F f,
            S s
    ) {
        return new Tuple<>(f, s);
    }
}