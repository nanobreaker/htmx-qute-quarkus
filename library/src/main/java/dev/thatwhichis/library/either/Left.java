package dev.thatwhichis.library.either;

public record Left<L, R>(L value) implements Either<L, R> {

}