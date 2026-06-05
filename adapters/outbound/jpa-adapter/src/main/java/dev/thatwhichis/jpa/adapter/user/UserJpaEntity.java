package dev.thatwhichis.jpa.adapter.user;

import dev.thatwhichis.core.domain.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static jakarta.persistence.FetchType.EAGER;

@Entity
public class UserJpaEntity {

    @Version
    private Integer version;

    @Id
    private UUID id;

    private String username;

    private Instant createdAt;
    private Instant touchedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private Set<UserSessionJpaEntity> sessions;

    public UserJpaEntity(
            final UUID id,
            final String username,
            final Instant createdAt,
            final Instant touchedAt,
            final Set<UserSessionJpaEntity> sessions
    ) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
        this.touchedAt = touchedAt;
        this.sessions = sessions;
    }

    public UserJpaEntity() {

    }

    public User into() {
        var sessions = this.sessions
                .stream()
                .map(UserSessionJpaEntity::into)
                .collect(Collectors.toSet());

        return new User.Builder(this.id)
                .withUsername(this.username)
                .withCreatedAt(this.createdAt)
                .withTouchedAt(this.touchedAt)
                .withSessions(sessions)
                .build();
    }

    public static UserJpaEntity from(final User user) {
        var sessions = user
                .getSessions()
                .stream()
                .map(UserSessionJpaEntity::from)
                .collect(Collectors.toSet());

        return new UserJpaEntity(
                user.getId(),
                user.getUsername(),
                user.getCreatedAt(),
                user.getTouchedAt(),
                sessions
        );
    }
}
