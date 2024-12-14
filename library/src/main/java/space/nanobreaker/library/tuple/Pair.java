package space.nanobreaker.library.tuple;

public record Pair<F, S>(F first, S second) {

    public static <F, S> Pair<F, S> of(
            F f,
            S s
    ) {
        return new Pair<>(f, s);
    }
}