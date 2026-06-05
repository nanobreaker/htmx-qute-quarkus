package dev.thatwhichis.jpa.adapter.todo;

import dev.thatwhichis.core.domain.todo.Todo;
import dev.thatwhichis.core.domain.todo.TodoId;
import dev.thatwhichis.core.ports.inbound.todo.TodoCommand;
import dev.thatwhichis.library.option.Option;
import io.github.dcadea.jresult.Result;
import io.quarkus.test.TestReactiveTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.UniAsserter;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static dev.thatwhichis.library.option.Option.none;
import static dev.thatwhichis.library.option.Option.some;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class TodoJpaRepositoryTest {

    @Inject
    TodoJpaRepository repository;

    @Test
    @TestReactiveTransaction
    public void should_save_todo(UniAsserter asserter) {
        var todo = new Todo(
                new TodoId(0, UUID.randomUUID()),
                "title",
                "description",
                Instant.now(),
                Instant.now()
        );

        asserter.assertThat(
                () -> repository.save(todo),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(todo)
        );
    }

    @Test
    @TestReactiveTransaction
    public void should_find_existing_todo(UniAsserter asserter) {
        var todo = new Todo(
                new TodoId(0, UUID.randomUUID()),
                "title",
                "description",
                Instant.now(),
                Instant.now()
        );

        asserter.execute(() -> repository.save(todo));

        asserter.assertThat(
                () -> repository.find(todo.getId()),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(Option.some(todo))
        );
    }

    @Test
    @TestReactiveTransaction
    public void should_list_todos_by_user_id(UniAsserter asserter) {
        var todo1 = new Todo(
                new TodoId(0, UUID.randomUUID()),
                "title",
                "description",
                Instant.now(),
                Instant.now()
        );
        var todo2 = new Todo(
                new TodoId(0, UUID.randomUUID()),
                "title",
                "description",
                Instant.now(),
                Instant.now()
        );

        asserter.execute(() -> repository.save(todo1));
        asserter.execute(() -> repository.save(todo2));

        asserter.assertThat(
                () -> repository.list(todo1.getId().userId()),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(Set.of(todo1))
        );
    }

    @Test
    @TestReactiveTransaction
    public void should_list_todos_by_user_id_and_filters(UniAsserter asserter) {
        var todo1 = new Todo(
                new TodoId(0, UUID.randomUUID()),
                "title1",
                "description",
                Instant.now(),
                Instant.now()
        );
        var todo2 = new Todo(
                new TodoId(0, UUID.randomUUID()),
                "title2",
                "description",
                Instant.now(),
                Instant.now()
        );

        asserter.execute(() -> repository.save(todo1));
        asserter.execute(() -> repository.save(todo2));

        asserter.assertThat(
                () -> repository.list(todo1.getId().userId(), Set.of("title1")),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(Set.of(todo1))
        );
    }

    @Test
    @TestReactiveTransaction
    public void should_list_todos_by_ids(UniAsserter asserter) {
        var userId1 = UUID.randomUUID();
        var userId2 = UUID.randomUUID();
        var todo1 = new Todo(
                new TodoId(0, userId1),
                "title1",
                "description",
                Instant.now(),
                Instant.now()
        );
        var todo2 = new Todo(
                new TodoId(1, userId1),
                "title2",
                "description",
                Instant.now(),
                Instant.now()
        );
        var todo3 = new Todo(
                new TodoId(0, userId2),
                "title3",
                "description",
                Instant.now(),
                Instant.now()
        );
        var todo4 = new Todo(
                new TodoId(1, userId2),
                "title4",
                "description",
                Instant.now(),
                Instant.now()
        );

        asserter.execute(() -> repository.save(todo1));
        asserter.execute(() -> repository.save(todo2));
        asserter.execute(() -> repository.save(todo3));
        asserter.execute(() -> repository.save(todo4));
        asserter.assertThat(
                () -> repository.list(Set.of(todo2.getId(), todo4.getId())),
                result -> assertThat(result.ok()).usingRecursiveComparison().isEqualTo(Optional.of(Set.of(todo2, todo4)))
        );
    }

    @Test
    @TestReactiveTransaction
    public void should_list_todos_by_ids_and_filters(UniAsserter asserter) {
        var userId1 = UUID.randomUUID();
        var userId2 = UUID.randomUUID();
        var todo1 = new Todo(
                new TodoId(0, userId1),
                "title1",
                "description",
                Instant.now(),
                Instant.now()
        );
        var todo2 = new Todo(
                new TodoId(1, userId1),
                "title2",
                "description",
                Instant.now(),
                Instant.now()
        );
        var todo3 = new Todo(
                new TodoId(0, userId2),
                "title3",
                "description",
                Instant.now(),
                Instant.now()
        );
        var todo4 = new Todo(
                new TodoId(1, userId2),
                "title4",
                "description",
                Instant.now(),
                Instant.now()
        );

        asserter.execute(() -> repository.save(todo1));
        asserter.execute(() -> repository.save(todo2));
        asserter.execute(() -> repository.save(todo3));
        asserter.execute(() -> repository.save(todo4));

        asserter.assertThat(
                () -> repository.list(Set.of(todo2.getId(), todo4.getId()), Set.of("title2")),
                result -> assertThat(result.ok()).usingRecursiveComparison().isEqualTo(Optional.of(Set.of(todo2)))
        );
    }

    @Test
    @TestReactiveTransaction
    public void should_update_single_field(UniAsserter asserter) {
        var todo = new Todo(
                new TodoId(0, UUID.randomUUID()),
                "title",
                "description",
                Instant.now(),
                Instant.now()
        );

        asserter.execute(() -> repository.save(todo));

        var payload = new TodoCommand.Update.Payload(
                none(),
                none(),
                none(),
                some(Instant.now().plusSeconds(86_400))
        );

        var updated = new Todo(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription().value(),
                todo.getStart().value(),
                payload.end().value()
        );

        asserter.execute(() -> repository.update(Set.of(todo.getId()), payload));

        asserter.assertThat(
                () -> repository.find(todo.getId()),
                result -> assertThat(result.ok()).usingRecursiveComparison().isEqualTo(Optional.of(some(updated)))
        );
    }

    @Test
    @TestReactiveTransaction
    public void should_update_all_fields(UniAsserter asserter) {
        var todo = new Todo(
                new TodoId(0, UUID.randomUUID()),
                "title",
                "description",
                Instant.now(),
                Instant.now()
        );

        asserter.execute(() -> repository.save(todo));

        var payload = new TodoCommand.Update.Payload(
                some("new title"),
                some("new description"),
                some(Instant.now().plusSeconds(86_400)),
                some(Instant.now().plusSeconds(86_400))
        );
        var updated = new Todo(
                todo.getId(),
                payload.title().value(),
                payload.description().value(),
                payload.start().value(),
                payload.end().value()
        );

        asserter.execute(() -> repository.update(Set.of(todo.getId()), payload));

        asserter.assertThat(
                () -> repository.find(todo.getId()),
                result -> assertThat(result.ok()).usingRecursiveComparison().isEqualTo(Optional.of(some(updated)))
        );
    }

    @Test
    @TestReactiveTransaction
    public void should_delete_todo(UniAsserter asserter) {
        var todo = new Todo(
                new TodoId(0, UUID.randomUUID()),
                "title",
                "description",
                Instant.now(),
                Instant.now()
        );

        asserter.execute(() -> repository.save(todo));

        asserter.assertThat(
                () -> repository.delete(todo.getId()),
                result -> assertThat(result).isEqualTo(Result.empty())
        );
        asserter.assertThat(
                () -> repository.find(todo.getId()),
                result -> {
                    assertThat(result.isOk()).isTrue();
                    assertThat(result.unwrap()).isEqualTo(Option.none());
                }
        );
    }

    @Test
    @TestReactiveTransaction
    public void should_delete_todos(UniAsserter asserter) {
        var userId = UUID.randomUUID();
        var todo1 = new Todo(
                new TodoId(0, userId),
                "title",
                "description",
                Instant.now(),
                Instant.now()
        );
        var todo2 = new Todo(
                new TodoId(1, userId),
                "title",
                "description",
                Instant.now(),
                Instant.now()
        );

        asserter.execute(() -> repository.save(todo1));
        asserter.execute(() -> repository.save(todo2));

        asserter.assertThat(
                () -> repository.delete(Set.of(todo1.getId(), todo2.getId())),
                result -> assertThat(result).isEqualTo(Result.empty())
        );
        asserter.assertThat(
                () -> repository.list(Set.of(todo1.getId(), todo2.getId())),
                result -> {
                    assertThat(result.isOk()).isTrue();
                    assertThat(result.unwrap()).isEqualTo(Set.of());
                }
        );
    }

    @Test
    @TestReactiveTransaction
    public void should_delete_all_todos(UniAsserter asserter) {
        var userId = UUID.randomUUID();
        var todo1 = new Todo(
                new TodoId(0, userId),
                "title",
                "description",
                Instant.now(),
                Instant.now()
        );
        var todo2 = new Todo(
                new TodoId(1, userId),
                "title",
                "description",
                Instant.now(),
                Instant.now()
        );

        asserter.execute(() -> repository.save(todo1));
        asserter.execute(() -> repository.save(todo2));

        asserter.assertThat(
                () -> repository.deleteAll(userId),
                result -> assertThat(result).isEqualTo(Result.empty())
        );
        asserter.assertThat(
                () -> repository.list(userId),
                result -> assertThat(result.ok()).usingRecursiveComparison().isEqualTo(Optional.of(Set.of()))
        );
    }
}