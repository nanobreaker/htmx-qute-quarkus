package space.nanobreaker.jpa.user;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserEntity> {

    public Uni<UserEntity> findByUsername(String username) {
        return find("username", username).firstResult();
    }

}
