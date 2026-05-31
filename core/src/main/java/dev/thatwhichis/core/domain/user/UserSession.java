package dev.thatwhichis.core.domain.user;

import java.time.Instant;

public record UserSession(
        String id,
        Instant createdAt,
        Instant expiresAt
) {

}
