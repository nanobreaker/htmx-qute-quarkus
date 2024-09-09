package space.nanobreaker.library;

public record Right<L, R>(R value) implements Either<L, R> {

}