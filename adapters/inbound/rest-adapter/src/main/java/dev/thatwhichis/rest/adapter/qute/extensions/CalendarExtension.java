package dev.thatwhichis.rest.adapter.qute.extensions;

import dev.thatwhichis.core.domain.calendar.Calendar;
import dev.thatwhichis.core.domain.calendar.CalendarEntry;
import dev.thatwhichis.library.option.None;
import dev.thatwhichis.library.option.Option;
import dev.thatwhichis.library.option.Some;
import dev.thatwhichis.library.tuple.Pair;
import io.quarkus.qute.TemplateExtension;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dev.thatwhichis.library.option.Option.none;

@TemplateExtension(namespace = "calendar")
public class CalendarExtension {

    //@formatter:off
    record Day(
            DayOfWeek dayOfWeek,
            int dayOfMonth,
            int dayOfYear,
            int weekOfMonth,
            Set<Integer> ids
    ) { }

    record Week(
            TreeMap<DayOfWeek, Option<Day>> days
    ) { }

    record CalendarView(
            Year year,
            Month month,
            LinkedHashMap<Integer, Week> weeks
    ) { }
    //@formatter:on

    public static CalendarView view(
            final Calendar calendar,
            final ZoneId zoneId
    ) {
        var currentZonedDateTime = ZonedDateTime.now().withZoneSameLocal(zoneId);
        var currentYear = Year.of(currentZonedDateTime.getYear());
        var currentMonth = currentZonedDateTime.getMonth();
        var currentMonthLength = currentMonth.length(currentYear.isLeap());
        var currentMonthFirstWeekIndex = LocalDate.of(currentYear.getValue(), currentMonth, 1).get(ChronoField.ALIGNED_WEEK_OF_MONTH);
        var currentMonthLastWeekIndex = LocalDate.of(currentYear.getValue(), currentMonth, currentMonthLength).get(ChronoField.ALIGNED_WEEK_OF_MONTH);

        var calendarView = new CalendarView(currentYear, currentMonth, new LinkedHashMap<>());

        var daysOfMonth = IntStream
                .rangeClosed(1, currentMonthLength)
                .mapToObj(index -> {
                    var day = LocalDate.of(currentYear.getValue(), currentMonth, index);
                    var dayOfWeek = day.getDayOfWeek();
                    var dayOfYear = day.getDayOfYear();
                    var weekOfYear = day.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
                    var ids = calendar
                            .getEntries()
                            .stream()
                            .filter(entry -> {
                                var startEnd = new Pair<>(entry.start(), entry.end());
                                return switch (startEnd) {
                                    case Pair(None(), None()) -> false;
                                    case Pair(Some(Instant start), None()) -> {
                                        var startLocalDate = start.atZone(zoneId).toLocalDate();

                                        yield day.isEqual(startLocalDate);
                                    }
                                    case Pair(None(), Some(Instant end)) -> {
                                        var endLocalDate = end.atZone(zoneId).toLocalDate();

                                        yield day.isEqual(endLocalDate);
                                    }
                                    case Pair(Some(Instant start), Some(Instant end)) -> {
                                        var startLocalDate = start.atZone(zoneId).toLocalDate();
                                        var endLocalDate = end.atZone(zoneId).toLocalDate();

                                        yield (day.isEqual(startLocalDate) || day.isAfter(startLocalDate))
                                                && (day.isEqual(endLocalDate) || day.isBefore(endLocalDate));
                                    }
                                };
                            })
                            .map(CalendarEntry::id)
                            .collect(Collectors.toSet());

                    return new Day(dayOfWeek, index, dayOfYear, weekOfYear, ids);
                })
                .sorted(Comparator.comparing(d -> d.dayOfMonth))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        IntStream
                .rangeClosed(currentMonthFirstWeekIndex, currentMonthLastWeekIndex)
                .forEach(weekOfMonth -> calendarView.weeks().put(
                        weekOfMonth,
                        new Week(new TreeMap<>(Map.of(
                                DayOfWeek.MONDAY, none(),
                                DayOfWeek.TUESDAY, none(),
                                DayOfWeek.WEDNESDAY, none(),
                                DayOfWeek.THURSDAY, none(),
                                DayOfWeek.FRIDAY, none(),
                                DayOfWeek.SATURDAY, none(),
                                DayOfWeek.SUNDAY, none()
                        ))))
                );

        daysOfMonth
                .stream()
                .forEach(day -> calendarView.weeks().get(day.weekOfMonth()).days().put(day.dayOfWeek, Option.some(day)));

        return calendarView;
    }
}
