package space.nanobreaker.library;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultTest {

    @Test
    void shouldFlattenNestedOkOk() {
        final String value = "test";
        final Result<Result<String, Exception>, Exception> result = Result.ok(Result.ok(value));
        final Result<String, Exception> flatten = result.flatten();

        assertTrue(flatten.isOk());
        assertThat(flatten).isEqualTo(Result.ok(value));
    }

    @Test
    void shouldFlattenOkErr() {
        final Exception error = new Exception();
        final Result<Result<String, Exception>, Exception> result = Result.ok(Result.err(error));
        final Result<String, Exception> flatten = result.flatten();

        assertTrue(flatten.isErr());
        assertThat(flatten).isEqualTo(Result.err(error));
    }

    @Test
    void test() {
        final Result<String, Object> test = Result.ok("test");

        final String s = switch (test) {
            case Ok(String value) -> value.toLowerCase();
            case Err(Object error) -> error.toString();
        };



    }

}