package space.nanobreaker.user.core.domain;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.VertxContextSupport;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import space.nanobreaker.user.jpa.UserEntity;

@ApplicationScoped
public class UserConfiguration {

    @ConfigProperty(name = "default.user.enabled", defaultValue = "false")
    Boolean defaultUserEnabled;

    @Inject
    UserService userService;

    void onStart(@Observes StartupEvent startupEvent) throws Throwable {
        VertxContextSupport.subscribeAndAwait(() -> defaultUserEnabled ? createDefaultUser().replaceWithVoid() : Uni.createFrom().nullItem());
    }

    private Uni<UserEntity> createDefaultUser() {
        final UserEntity defaultUser = new UserEntity();
        defaultUser.setUsername("admin");
        defaultUser.setPassword("admin");
        return userService.createUser(defaultUser);
    }

}
