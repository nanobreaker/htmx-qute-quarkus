package space.nanobreaker.library;

public record Left<L, R>(L value) implements Either<L, R> {
}
