package space.nanobreaker.library.either;

public record Left<L, R>(L value) implements Either<L, R> {

}