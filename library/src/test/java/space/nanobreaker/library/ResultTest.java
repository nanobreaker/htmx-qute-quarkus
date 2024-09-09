package space.nanobreaker.library;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultTest {

    record ResultTestError() implements Error {
    }

    @Test
    void shouldReturnOk() {
        final Result<String, Error> result = Result.ok("test");
        assertThat(result.isOk()).isEqualTo(true);
        assertThat(result.isErr()).isEqualTo(false);
        assertThat(result.unwrap()).isEqualTo("test");
        assertThatRuntimeException().isThrownBy(result::error)
                .withMessage("Ok can not be Err");
    }

    @Test
    void shouldReturnErr() {
        final Result<String, Error> result = Result.err(new ResultTestError());
        assertThat(result.isOk()).isEqualTo(false);
        assertThat(result.isErr()).isEqualTo(true);
        assertThat(result.error()).isEqualTo(new ResultTestError());
        assertThatRuntimeException().isThrownBy(result::unwrap)
                .withMessage("Err can not be unwrapped");
    }

    @Test
    void shouldFlattenNestedResultOfOkOk() {
        final String value = "test";
        final Result<Result<String, Exception>, Exception> result = Result.ok(Result.ok(value));
        final Result<String, Exception> flatten = result.flatten();

        assertTrue(flatten.isOk());
        assertThat(flatten).isEqualTo(Result.ok(value));
    }

    @Test
    void shouldFlattenNestedResultOfOkErr() {
        final Exception error = new Exception();
        final Result<Result<String, Exception>, Exception> result = Result.ok(Result.err(error));
        final Result<String, Exception> flatten = result.flatten();

        assertTrue(flatten.isErr());
        assertThat(flatten).isEqualTo(Result.err(error));
    }

}