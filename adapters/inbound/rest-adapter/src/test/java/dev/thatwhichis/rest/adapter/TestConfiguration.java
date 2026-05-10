package dev.thatwhichis.rest.adapter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@ApplicationScoped
public class TestConfiguration {

    @Produces
    @ApplicationScoped
    Clock clock() {
        return Clock.fixed(
                Instant.parse("2026-01-01T00:00:00Z"),
                ZoneOffset.UTC
        );
    }
}