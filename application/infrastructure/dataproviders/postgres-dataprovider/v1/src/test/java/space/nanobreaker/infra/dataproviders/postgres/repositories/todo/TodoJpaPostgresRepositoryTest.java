package space.nanobreaker.infra.dataproviders.postgres.repositories.todo;

import io.github.dcadea.jresult.Result;
import io.quarkus.test.TestReactiveTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.UniAsserter;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import space.nanobreaker.core.domain.v1.Command;
import space.nanobreaker.core.domain.v1.todo.Todo;
import space.nanobreaker.core.domain.v1.todo.TodoId;
import space.nanobreaker.library.option.Option;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static space.nanobreaker.library.option.Option.some;

@QuarkusTest
class TodoJpaPostgresRepositoryTest {

    @Inject
    TodoJpaPostgresRepository repository;

    @Test
    @TestReactiveTransaction
    public void save_todo(UniAsserter asserter) {
        var todo = new Todo(new TodoId(0, "test"), "title", "description", ZonedDateTime.now(), ZonedDateTime.now());

        asserter.assertThat(
                () -> repository.save(todo),
                result -> assertThat(result.ok()).usingRecursiveComparison().isEqualTo(Optional.of(todo))
        );
    }

    @Test
    @TestReactiveTransaction
    public void find_todo(UniAsserter asserter) {
        var todo = new Todo(new TodoId(0, "test"), "title", "description", ZonedDateTime.now(), ZonedDateTime.now());

        asserter.execute(() -> repository.save(todo));
        asserter.assertThat(
                () -> repository.find(todo.getId()),
                result -> assertThat(result.ok()).usingRecursiveComparison().isEqualTo(Optional.of(Option.some(todo)))
        );
    }

    @Test
    @TestReactiveTransaction
    public void list_by_username(UniAsserter asserter) {
        var todo1 = new Todo(new TodoId(0, "test"), "title", "description", ZonedDateTime.now(), ZonedDateTime.now());
        var todo2 = new Todo(new TodoId(0, "asda"), "title", "description", ZonedDateTime.now(), ZonedDateTime.now());

        asserter.execute(() -> repository.save(todo1));
        asserter.execute(() -> repository.save(todo2));
        asserter.assertThat(
                () -> repository.list("test"),
                result -> assertThat(result.ok()).usingRecursiveComparison().isEqualTo(Optional.of(Set.of(todo1)))
        );
    }

    @Test
    @TestReactiveTransaction
    public void list_by_username_and_filters(UniAsserter asserter) {
        var todo1 = new Todo(new TodoId(0, "test"), "title1", "description", ZonedDateTime.now(), ZonedDateTime.now());
        var todo2 = new Todo(new TodoId(0, "asda"), "title2", "description", ZonedDateTime.now(), ZonedDateTime.now());

        asserter.execute(() -> repository.save(todo1));
        asserter.execute(() -> repository.save(todo2));
        asserter.assertThat(
                () -> repository.list("test", Set.of("title1")),
                result -> assertThat(result.ok()).usingRecursiveComparison().isEqualTo(Optional.of(Set.of(todo1)))
        );
    }

    @Test
    @TestReactiveTransaction
    public void list_by_ids(UniAsserter asserter) {
        var todo1 = new Todo(new TodoId(0, "alice"), "title1", "description", ZonedDateTime.now(), ZonedDateTime.now());
        var todo2 = new Todo(new TodoId(1, "alice"), "title2", "description", ZonedDateTime.now(), ZonedDateTime.now());
        var todo3 = new Todo(new TodoId(0, "bobie"), "title3", "description", ZonedDateTime.now(), ZonedDateTime.now());
        var todo4 = new Todo(new TodoId(1, "bobie"), "title4", "description", ZonedDateTime.now(), ZonedDateTime.now());

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
    public void list_by_ids_and_filters(UniAsserter asserter) {
        var todo1 = new Todo(new TodoId(0, "alice"), "title1", "description", ZonedDateTime.now(), ZonedDateTime.now());
        var todo2 = new Todo(new TodoId(1, "alice"), "title2", "description", ZonedDateTime.now(), ZonedDateTime.now());
        var todo3 = new Todo(new TodoId(0, "bobie"), "title3", "description", ZonedDateTime.now(), ZonedDateTime.now());
        var todo4 = new Todo(new TodoId(1, "bobie"), "title4", "description", ZonedDateTime.now(), ZonedDateTime.now());

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
    public void update_todo(UniAsserter asserter) {
        var todo = new Todo(new TodoId(0, "test"), "title", "description", ZonedDateTime.now(), ZonedDateTime.now());
        asserter.execute(() -> repository.save(todo));

        var payload = new Command.Todo.Update.Payload(
                some("new title"),
                some("new description"),
                some(ZonedDateTime.now().plusDays(1)),
                some(ZonedDateTime.now().plusDays(1))
        );
        asserter.execute(() -> repository.update(Set.of(todo), payload));

        asserter.assertThat(
                () -> repository.find(todo.getId()),
                result -> assertThat(result.ok()).usingRecursiveComparison().isEqualTo(Optional.of(some(todo)))
        );
    }

    @Test
    @TestReactiveTransaction
    public void delete_todo(UniAsserter asserter) {
        var todo = new Todo(new TodoId(0, "test"), "title", "description", ZonedDateTime.now(), ZonedDateTime.now());

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
    public void delete_todos(UniAsserter asserter) {
        var todo1 = new Todo(new TodoId(0, "test"), "title", "description", ZonedDateTime.now(), ZonedDateTime.now());
        var todo2 = new Todo(new TodoId(1, "test"), "title", "description", ZonedDateTime.now(), ZonedDateTime.now());
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
    public void delete_all(UniAsserter asserter) {
        var todo1 = new Todo(new TodoId(0, "test"), "title", "description", ZonedDateTime.now(), ZonedDateTime.now());
        var todo2 = new Todo(new TodoId(1, "test"), "title", "description", ZonedDateTime.now(), ZonedDateTime.now());
        asserter.execute(() -> repository.save(todo1));
        asserter.execute(() -> repository.save(todo2));

        asserter.assertThat(
                () -> repository.deleteAll("test"),
                result -> assertThat(result).isEqualTo(Result.empty())
        );
        asserter.assertThat(
                () -> repository.list("test"),
                result -> assertThat(result.ok()).usingRecursiveComparison().isEqualTo(Optional.of(Set.of()))
        );
    }
}