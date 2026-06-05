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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class CalendarEntryJpaRepositoryTest {

    @Inject
    CalendarJpaRepository calendarRepository;

    @Inject
    CalendarEntryJpaRepository repository;

    @Test
    @TestReactiveTransaction
    void should_save_calendar_entry(UniAsserter asserter) {
        var calendar = new Calendar.Builder(UUID.randomUUID()).withNoEntries().build();
        var entry = new CalendarEntry(0, calendar.getId(), Option.some(Instant.now()), Option.none());

        asserter.execute(() -> calendarRepository.save(calendar));

        asserter.assertThat(
                () -> repository.save(entry),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(entry)
        );
    }

    @Test
    @TestReactiveTransaction
    void should_get_calendar_entry_by_id(UniAsserter asserter) {
        var calendar = new Calendar.Builder(UUID.randomUUID()).withNoEntries().build();
        var entry = new CalendarEntry(0, calendar.getId(), Option.some(Instant.now()), Option.none());

        asserter.execute(() -> calendarRepository.save(calendar));
        asserter.execute(() -> repository.save(entry));

        asserter.assertThat(
                () -> repository.get(entry.id()),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(entry)
        );
    }

    @Test
    @TestReactiveTransaction
    void should_update_calendar_entry_by_id(UniAsserter asserter) {
        var calendar = new Calendar.Builder(UUID.randomUUID()).withNoEntries().build();
        var entry = new CalendarEntry(0, calendar.getId(), Option.some(Instant.now()), Option.none());
        var updated = new CalendarEntry(entry.id(), calendar.getId(), Option.none(), Option.some(Instant.now()));

        asserter.execute(() -> calendarRepository.save(calendar));
        asserter.execute(() -> repository.save(entry));

        asserter.assertThat(
                () -> repository.update(updated),
                result -> assertThat(result.isOk()).isTrue()
        );

        asserter.assertThat(
                () -> repository.get(entry.id()),
                result -> assertThat(result.unwrap()).usingRecursiveComparison().isEqualTo(updated)
        );
    }

    @Test
    @TestReactiveTransaction
    void should_delete_calendar_entry_by_id(UniAsserter asserter) {
        var calendar = new Calendar.Builder(UUID.randomUUID()).withNoEntries().build();
        var entry = new CalendarEntry(0, calendar.getId(), Option.some(Instant.now()), Option.none());

        asserter.execute(() -> calendarRepository.save(calendar));
        asserter.execute(() -> repository.save(entry));

        asserter.assertThat(
                () -> repository.delete(entry.id()),
                result -> assertThat(result.isOk()).isTrue()
        );

        asserter.assertThat(
                () -> repository.get(entry.id()),
                result -> assertThat(result.unwrapErr()).usingRecursiveComparison().isEqualTo(new JpaError.EntityNotFound())
        );
    }

    @Test
    @TestReactiveTransaction
    void should_delete_all_entries_by_calendar_id(UniAsserter asserter) {
        var calendar = new Calendar.Builder(UUID.randomUUID()).withNoEntries().build();
        var entry1 = new CalendarEntry(0, calendar.getId(), Option.some(Instant.now()), Option.none());
        var entry2 = new CalendarEntry(1, calendar.getId(), Option.some(Instant.now()), Option.none());

        asserter.execute(() -> calendarRepository.save(calendar));
        asserter.execute(() -> repository.save(entry1));
        asserter.execute(() -> repository.save(entry2));

        asserter.assertThat(
                () -> repository.deleteAll(calendar.getId()),
                result -> assertThat(result.isOk()).isTrue()
        );

        asserter.assertThat(
                () -> repository.get(entry1.id()),
                result -> assertThat(result.unwrapErr()).usingRecursiveComparison().isEqualTo(new JpaError.EntityNotFound())
        );
        asserter.assertThat(
                () -> repository.get(entry2.id()),
                result -> assertThat(result.unwrapErr()).usingRecursiveComparison().isEqualTo(new JpaError.EntityNotFound())
        );
    }
}