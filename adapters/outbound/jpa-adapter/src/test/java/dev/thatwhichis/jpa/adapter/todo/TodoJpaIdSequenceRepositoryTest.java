package dev.thatwhichis.jpa.adapter.todo;

import dev.thatwhichis.core.domain.todo.TodoId;
import io.quarkus.test.TestReactiveTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.UniAsserter;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class TodoJpaIdSequenceRepositoryTest {

    @Inject
    TodoJpaIdSequenceRepository repository;

    @Test
    @TestReactiveTransaction
    public void should_create_sequence_on_first_call(UniAsserter asserter) {
        var userId = UUID.randomUUID();
        var todoId = new TodoId(1, userId);

        asserter.assertThat(
                () -> repository.next(userId),
                result -> assertThat(result).usingRecursiveComparison().isEqualTo(todoId)
        );
    }

    @Test
    @TestReactiveTransaction
    public void should_increment_sequence_on_consequent_call(UniAsserter asserter) {
        var userId = UUID.randomUUID();
        var todoId = new TodoId(2, userId);

        asserter.execute(() -> repository.next(userId));

        asserter.assertThat(
                () -> repository.next(userId),
                result -> assertThat(result).usingRecursiveComparison().isEqualTo(todoId)
        );
    }
}