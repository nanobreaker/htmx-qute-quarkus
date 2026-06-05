package dev.thatwhichis.core.domain.user;

import dev.thatwhichis.framework.entity.Entity;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class User extends Entity<UUID> {

    private final String username;
    private final Instant createdAt;
    private Instant touchedAt;
    private Set<UserSession> sessions;

    protected User(
            final UUID id,
            final String username,
            final Instant createdAt,
            final Instant touchedAt,
            final Set<UserSession> sessions
    ) {
        super(id);
        this.username = username;
        this.createdAt = createdAt;
        this.touchedAt = touchedAt;
        this.sessions = sessions;
    }

    public String getUsername() {
        return username;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getTouchedAt() {
        return touchedAt;
    }

    public Set<UserSession> getSessions() {
        return sessions;
    }

    public void setTouchedAt(Instant touchedAt) {
        this.touchedAt = touchedAt;
    }

    public void updateSession(UserSession session) {
        this.sessions.add(session);
    }

    public static final class Builder {

        private final UUID id;
        private String username;
        private Instant createdAt;
        private Instant touchedAt;
        private Set<UserSession> sessions;

        public Builder(UUID id) {
            this.id = id;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withSessions(Set<UserSession> sessions) {
            this.sessions = sessions;
            return this;
        }

        public Builder withCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withTouchedAt(Instant touchedAt) {
            this.touchedAt = touchedAt;
            return this;
        }

        public User build() {
            return new User(
                    id,
                    username,
                    createdAt,
                    touchedAt,
                    sessions
            );
        }
    }
}
