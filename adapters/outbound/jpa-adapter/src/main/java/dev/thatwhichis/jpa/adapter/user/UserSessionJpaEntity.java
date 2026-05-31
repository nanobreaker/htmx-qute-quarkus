package dev.thatwhichis.jpa.adapter.user;

import dev.thatwhichis.core.domain.user.UserSession;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class UserSessionJpaEntity {

    @Id
    private String id;

    private Instant createdAt;
    private Instant expiresAt;

    public UserSessionJpaEntity(
            final String id,
            final Instant expiresAt,
            final Instant createdAt
    ) {
        this.id = id;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public UserSessionJpaEntity() {

    }

    public UserSession into() {
        return new UserSession(id, createdAt, expiresAt);
    }

    public static UserSessionJpaEntity from(final UserSession userSession) {
        return new UserSessionJpaEntity(
                userSession.id(),
                userSession.expiresAt(),
                userSession.createdAt()
        );
    }
}
