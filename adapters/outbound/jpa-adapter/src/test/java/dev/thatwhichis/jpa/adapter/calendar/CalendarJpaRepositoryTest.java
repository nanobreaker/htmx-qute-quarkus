package dev.thatwhichis.jpa.adapter.calendar;

import dev.thatwhichis.core.domain.calendar.Calendar;
import dev.thatwhichis.core.domain.calendar.CalendarEntry;
import dev.thatwhichis.library.error.JpaError;
import dev.thatwhichis.library.option.Option;
import io.quarkus.test.TestReactiveTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.UniAsserter;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class CalendarJpaRepositoryTest {

    @Inject
    CalendarJpaRepository repository;

    @Test
    @TestReactiveTransaction
    void should_save_calendar(UniAsserter asserter) {
        var entry = new CalendarEntry(0, Option.some(Instant.now()), Option.none());
        var calendar = new Calendar.Builder(UUID.randomUUID())
                .withEntries(Set.of(entry))
                .build();

        asserter.assertThat(
                () -> repository.save(calendar),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(calendar)
        );
    }

    @Test
    @TestReactiveTransaction
    void should_get_existing_calendar_by_uuid(UniAsserter asserter) {
        var entry = new CalendarEntry(0, Option.some(Instant.now()), Option.none());
        var calendar = new Calendar.Builder(UUID.randomUUID())
                .withEntries(Set.of(entry))
                .build();

        asserter.execute(() -> repository.save(calendar));
        asserter.assertThat(
                () -> repository.get(calendar.getId()),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(calendar)
        );
    }

    @Test
    @TestReactiveTransaction
    void should_not_get_non_existing_calendar_by_uuid(UniAsserter asserter) {
        asserter.assertThat(
                () -> repository.get(UUID.randomUUID()),
                result -> assertThat(result.unwrapErr()).usingRecursiveComparison().isEqualTo(new JpaError.EntityNotFound())
        );
    }

    @Test
    @TestReactiveTransaction
    void should_find_existing_calendar_by_uuid(UniAsserter asserter) {
        var entry = new CalendarEntry(0, Option.some(Instant.now()), Option.none());
        var calendar = new Calendar.Builder(UUID.randomUUID())
                .withEntries(Set.of(entry))
                .build();

        asserter.execute(() -> repository.save(calendar));
        asserter.assertThat(
                () -> repository.find(calendar.getId()),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(Option.some(calendar))
        );
    }

    @Test
    @TestReactiveTransaction
    void should_not_find_non_existing_calendar_by_uuid(UniAsserter asserter) {
        asserter.assertThat(
                () -> repository.find(UUID.randomUUID()),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(Option.none())
        );
    }

    @Test
    @TestReactiveTransaction
    void should_update_calendar(UniAsserter asserter) {
        var firstEntry = new CalendarEntry(0, Option.some(Instant.now()), Option.none());
        var secondEntry = new CalendarEntry(1, Option.none(), Option.none());
        var entries = new HashSet<>(Set.of(firstEntry, secondEntry));

        var calendar = new Calendar.Builder(UUID.randomUUID())
                .withEntries(entries)
                .build();

        asserter.execute(() -> repository.save(calendar));

        entries.remove(secondEntry);
        entries.add(new CalendarEntry(2, Option.some(Instant.now()), Option.some(Instant.now())));
        calendar.setEntries(entries);

        asserter.execute(() -> repository.save(calendar));

        asserter.assertThat(
                () -> repository.get(calendar.getId()),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(calendar)
        );
    }
}