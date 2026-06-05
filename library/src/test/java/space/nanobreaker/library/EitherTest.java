package space.nanobreaker.library;

import dev.thatwhichis.library.either.Either;
import dev.thatwhichis.library.either.Left;
import dev.thatwhichis.library.either.Right;
import dev.thatwhichis.library.option.Option;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class EitherTest {

    @Test
    void shouldReturnTrueIfLeftVariant() {
        final Option<ZonedDateTime> option = Option.some(ZonedDateTime.now());
        option.map(ZonedDateTime::toString).orElse("");

        final Either<Integer, Integer> left = new Left<>(42);
        assertThat(left.isLeft()).isEqualTo(true);

        final Either<Integer, Integer> right = new Right<>(666);
        assertThat(right.isLeft()).isEqualTo(false);
    }

    @Test
    void shouldReturnTrueIfRightVariant() {
        final Either<Integer, Integer> left = new Left<>(666);
        assertThat(left.isRight()).isEqualTo(false);

        final Either<Integer, Integer> right = new Right<>(42);
        assertThat(right.isRight()).isEqualTo(true);
    }

    @Test
    void shouldConvertLeftSideOfEitherToOption() {
        final Either<Integer, Integer> left = new Left<>(42);
        assertThat(left.left()).isEqualTo(Option.some(42));

        final Either<Integer, Integer> right = new Right<>(666);
        assertThat(right.left()).isEqualTo(Option.none());
    }

    @Test
    void shouldConvertRightSideOfEitherToOption() {
        final Either<Integer, Integer> right = new Right<>(42);
        assertThat(right.right()).isEqualTo(Option.some(42));

        final Either<Integer, Integer> left = new Left<>(666);
        assertThat(left.right()).isEqualTo(Option.none());
    }

    @Test
    void shouldFlipLeftWithRightSide() {
        final Either<Integer, Integer> left = new Left<>(42);
        assertThat(left.flip()).isEqualTo(new Right<>(42));

        final Either<Integer, Integer> right = new Right<>(666);
        assertThat(right.flip()).isEqualTo(new Left<>(666));
    }

    @Test
    void shouldApplyFunctionToLeftSideIfPresent() {
        final Either<Integer, Integer> left = new Left<>(42);
        assertThat(left.mapLeft(l -> l * 2)).isEqualTo(new Left<>(84));

        final Either<Integer, Integer> right = new Right<>(42);
        assertThat(right.mapLeft(l -> l * 2)).isEqualTo(new Right<>(42));
    }

    @Test
    void shouldApplyFunctionToRightSideIfPresent() {
        final Either<Integer, Integer> right = new Right<>(42);
        assertThat(right.mapRight(l -> l * 2)).isEqualTo(new Right<>(84));

        final Either<Integer, Integer> left = new Left<>(42);
        assertThat(left.mapRight(l -> l * 2)).isEqualTo(new Left<>(42));
    }

    @Test
    void shouldApplyFunctionsToBothSidesIfPresent() {
        final Either<String, String> left = new Left<>("TeSt");
        assertThat(left.mapEither(String::toLowerCase, String::length)).isEqualTo(new Left<>("test"));

        final Either<String, String> right = new Right<>("TeSt");
        assertThat(right.mapEither(String::toLowerCase, String::length)).isEqualTo(new Right<>(4));
    }
}
