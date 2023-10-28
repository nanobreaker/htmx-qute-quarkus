package space.nanobreaker.infra.dataproviders.postgres.repositories;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import space.nanobreaker.core.domain.v1.User;
import space.nanobreaker.core.usecases.repositories.v1.UserRepository;

import java.util.UUID;

public class PostgresPanacheUserRepository implements UserRepository, PanacheRepositoryBase<User, UUID> {

    @Override
    public Uni<User> persist(User userEntity) {
        return PanacheRepositoryBase.super.persist(userEntity);
    }

    public Uni<User> findByUserId(UUID id) {
        return findById(id);
    }

    @Override
    public Uni<User> findByUsername(String username) {
        return find("username", username).firstResult();
    }

}
