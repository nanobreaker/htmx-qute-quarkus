package space.nanobreaker.core.usecases.v1.user.handler;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.User;
import space.nanobreaker.core.usecases.repositories.v1.UserRepository;
import space.nanobreaker.core.usecases.v1.QueryHandler;
import space.nanobreaker.core.usecases.v1.user.query.GetUserQuery;

@ApplicationScoped
public class GetUserQueryHandler implements QueryHandler<GetUserQuery, User> {

    @Inject
    @Any
    UserRepository userRepository;

    @Override
    @ConsumeEvent(value = "getUserQuery")
    @WithSession
    public Uni<User> execute(GetUserQuery query) {
        return userRepository.findByUserId(query.id());
    }
}
