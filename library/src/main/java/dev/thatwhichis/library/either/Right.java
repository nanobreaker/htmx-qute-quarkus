package dev.thatwhichis.library.either;

public record Right<L, R>(R value) implements Either<L, R> {

}