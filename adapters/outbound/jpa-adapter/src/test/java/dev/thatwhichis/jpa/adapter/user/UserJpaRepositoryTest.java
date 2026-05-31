package dev.thatwhichis.jpa.adapter.user;

import dev.thatwhichis.core.domain.user.User;
import dev.thatwhichis.core.domain.user.UserSession;
import dev.thatwhichis.library.error.JpaError;
import dev.thatwhichis.library.option.Option;
import io.quarkus.test.TestReactiveTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.UniAsserter;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class UserJpaRepositoryTest {

    @Inject
    UserJpaRepository repository;

    @Test
    @TestReactiveTransaction
    void should_save_user(UniAsserter asserter) {
        var session = new UserSession("test-sid", Instant.now(), Instant.now());
        var user = new User.Builder(UUID.randomUUID())
                .withUsername("test")
                .withTouchedAt(Instant.now())
                .withCreatedAt(Instant.now())
                .withSessions(Set.of(session))
                .build();

        asserter.assertThat(
                () -> repository.save(user),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(user)
        );
    }

    @Test
    @TestReactiveTransaction
    void should_get_existing_user_by_uuid(UniAsserter asserter) {
        var session = new UserSession("test-sid", Instant.now(), Instant.now());
        var user = new User.Builder(UUID.randomUUID())
                .withUsername("test")
                .withTouchedAt(Instant.now())
                .withCreatedAt(Instant.now())
                .withSessions(Set.of(session))
                .build();

        asserter.execute(() -> repository.save(user));
        asserter.assertThat(
                () -> repository.get(user.getId()),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(user)
        );
    }

    @Test
    @TestReactiveTransaction
    void should_not_get_non_existing_user_by_uuid(UniAsserter asserter) {
        asserter.assertThat(
                () -> repository.get(UUID.randomUUID()),
                result -> assertThat(result.unwrapErr()).usingRecursiveComparison().isEqualTo(new JpaError.EntityNotFound())
        );
    }

    @Test
    @TestReactiveTransaction
    void should_find_existing_user_by_uuid(UniAsserter asserter) {
        var session = new UserSession("test-sid", Instant.now(), Instant.now());
        var user = new User.Builder(UUID.randomUUID())
                .withUsername("test")
                .withTouchedAt(Instant.now())
                .withCreatedAt(Instant.now())
                .withSessions(Set.of(session))
                .build();

        asserter.execute(() -> repository.save(user));
        asserter.assertThat(
                () -> repository.find(user.getId()),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(Option.some(user))
        );
    }

    @Test
    @TestReactiveTransaction
    void should_not_find_non_existing_user_by_uuid(UniAsserter asserter) {
        asserter.assertThat(
                () -> repository.find(UUID.randomUUID()),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(Option.none())
        );
    }
}