package space.nanobreaker.user.core.domain;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import space.nanobreaker.jpa.user.UserEntity;

@ApplicationScoped
public class UserConfiguration {

    @ConfigProperty(name = "default.user.enabled", defaultValue = "false")
    Boolean defaultUserEnabled;

    @Inject
    UserService userService;

    public Uni<Void> execute() {
        return defaultUserEnabled ? createDefaultUser().replaceWithVoid() : Uni.createFrom().nullItem();
    }

    private Uni<UserEntity> createDefaultUser() {
        final UserEntity defaultUser = new UserEntity();
        defaultUser.setUsername("admin");
        defaultUser.setPassword("admin");
        return userService.createUser(defaultUser);
    }

}
