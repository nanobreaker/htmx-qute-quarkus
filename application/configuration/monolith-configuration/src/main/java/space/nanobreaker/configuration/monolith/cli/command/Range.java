package space.nanobreaker.configuration.monolith.cli.command;

import java.time.LocalDateTime;

public record Range(LocalDateTime start, LocalDateTime end) {

    public static Range of(LocalDateTime start, LocalDateTime end) {
        return new Range(start, end);
    }

    public static Range until(LocalDateTime end) {
        return new Range(null, end);
    }

    public static Range from(LocalDateTime start) {
        return new Range(start, null);
    }

}