package space.nanobreaker.library.either;

public record Right<L, R>(R value) implements Either<L, R> {

}