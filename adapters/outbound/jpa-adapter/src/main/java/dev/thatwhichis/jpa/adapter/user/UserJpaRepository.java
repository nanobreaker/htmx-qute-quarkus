package dev.thatwhichis.jpa.adapter.user;

import dev.thatwhichis.core.domain.user.User;
import dev.thatwhichis.core.ports.outbound.user.UserRepository;
import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.library.error.JpaError;
import dev.thatwhichis.library.option.Option;
import io.github.dcadea.jresult.Result;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

import static io.github.dcadea.jresult.Result.err;

@ApplicationScoped
public class UserJpaRepository implements UserRepository, PanacheRepositoryBase<UserJpaEntity, UUID> {

    public Uni<Result<User, Error>> save(User user) {
        var jpaEntity = UserJpaEntity.from(user);

        return this.persistAndFlush(jpaEntity)
                .map(UserJpaEntity::into)
                .map(Result::<User, Error>ok)
                .onFailure().recoverWithItem(throwable -> err(new JpaError.Uncategorized(throwable)));
    }

    @Override
    public Uni<Result<User, Error>> get(UUID userId) {
        return this.findById(userId)
                .map(UserJpaEntity::into)
                .map(Result::<User, Error>ok)
                .onFailure().recoverWithItem(throwable -> switch (throwable) {
                    case NullPointerException _ -> err(new JpaError.EntityNotFound());
                    default -> err(new JpaError.Uncategorized(throwable));
                });
    }

    @Override
    public Uni<Result<Option<User>, Error>> find(UUID userId) {
        return this.findById(userId)
                .map(Option::some)
                .map(option -> option.map(UserJpaEntity::into))
                .map(Result::<Option<User>, Error>ok)
                .onFailure().recoverWithItem(throwable -> err(new JpaError.Uncategorized(throwable)));
    }
}
