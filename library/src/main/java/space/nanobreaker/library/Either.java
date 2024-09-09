package space.nanobreaker.library;

import java.util.function.Function;

public sealed interface Either<L, R> permits Left, Right {

    default boolean isLeft() {
        return switch (this) {
            case Left(L ignored) -> true;
            case Right(R ignored) -> false;
        };
    }

    default boolean isRight() {
        return switch (this) {
            case Left(L ignored) -> false;
            case Right(R ignored) -> true;
        };
    }

    default Option<L> left() {
        return switch (this) {
            case Left(L left) -> Option.of(left);
            case Right(R ignored) -> Option.none();
        };
    }

    default Option<R> right() {
        return switch (this) {
            case Left(L ignored) -> Option.none();
            case Right(R right) -> Option.of(right);
        };
    }

    default <NL> Either<NL, R> mapLeft(
            final Function<? super L, ? extends NL> valueMapper
    ) {
        return switch (this) {
            case Left(L left) -> new Left<>(valueMapper.apply(left));
            case Right(R right) -> new Right<>(right);
        };
    }

    default <NR> Either<L, NR> mapRight(
            final Function<? super R, ? extends NR> valueMapper
    ) {
        return switch (this) {
            case Left(L left) -> new Left<>(left);
            case Right(R right) -> new Right<>(valueMapper.apply(right));
        };
    }

    default <NL, NR> Either<NL, NR> mapEither(
            final Function<? super L, ? extends NL> leftMapper,
            final Function<? super R, ? extends NR> rightMapper
    ) {
        return switch (this) {
            case Left(L left) -> new Left<>(leftMapper.apply(left));
            case Right(R right) -> new Right<>(rightMapper.apply(right));
        };
    }

    default Either<R, L> flip() {
        return switch (this) {
            case Left(L left) -> new Right(left);
            case Right(R right) -> new Left(right);
        };
    }

}