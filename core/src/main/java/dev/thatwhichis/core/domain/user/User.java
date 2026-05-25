package dev.thatwhichis.core.domain.user;

import dev.thatwhichis.framework.ddd.Entity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class User extends Entity<UUID> {

    private final String username;
    private final List<String> sessions;

    private Integer todoCreatedCount;
    private Integer todoUpdatedCount;
    private Integer todoDeletedCount;

    private final Instant firstLogin;
    private Instant lastLogin;

    protected User(
            final UUID id,
            final String username,
            final List<String> sessions,
            final Integer todoCreatedCount,
            final Integer todoUpdatedCount,
            final Integer todoDeletedCount,
            final Instant firstLogin,
            final Instant lastLogin
    ) {
        super(id);
        this.username = username;
        this.sessions = sessions;
        this.todoCreatedCount = todoCreatedCount;
        this.todoUpdatedCount = todoUpdatedCount;
        this.todoDeletedCount = todoDeletedCount;
        this.firstLogin = firstLogin;
        this.lastLogin = lastLogin;
    }

    public void updateSession(String session) {
        this.sessions.add(session);
    }

    public void setLastLogin(Instant lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setTodoCreatedCount(Integer todoCreatedCount) {
        this.todoCreatedCount = todoCreatedCount;
    }

    public void setTodoUpdatedCount(Integer todoUpdatedCount) {
        this.todoUpdatedCount = todoUpdatedCount;
    }

    public void setTodoDeletedCount(Integer todoDeletedCount) {
        this.todoDeletedCount = todoDeletedCount;
    }

    public static final class Builder {

        private final UUID id;
        private String username;
        private List<String> sessions;
        private Integer todoCreatedCount;
        private Integer todoUpdatedCount;
        private Integer todoDeletedCount;
        private Instant lastLogin;
        private Instant firstLogin;

        public Builder(UUID id) {
            this.id = id;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withSessions(List<String> sessions) {
            this.sessions = sessions;
            return this;
        }

        public Builder withTodoCreatedCount(Integer todoCreatedCount) {
            this.todoCreatedCount = todoCreatedCount;
            return this;
        }

        public Builder withTodoUpdatedCount(Integer todoUpdatedCount) {
            this.todoUpdatedCount = todoUpdatedCount;
            return this;
        }

        public Builder withTodoDeletedCount(Integer todoDeletedCount) {
            this.todoDeletedCount = todoDeletedCount;
            return this;
        }

        public Builder withLastLogin(Instant lastLogin) {
            this.lastLogin = lastLogin;
            return this;
        }

        public Builder withFirstLogin(Instant firstLogin) {
            this.firstLogin = firstLogin;
            return this;
        }

        public User build() {
            return new User(
                    id,
                    username,
                    sessions,
                    todoCreatedCount,
                    todoUpdatedCount,
                    todoDeletedCount,
                    lastLogin,
                    firstLogin
            );
        }
    }
}
