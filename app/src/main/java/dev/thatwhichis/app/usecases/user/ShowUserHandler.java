package dev.thatwhichis.app.usecases.user;

import dev.thatwhichis.core.domain.user.User;
import dev.thatwhichis.core.ports.inbound.user.UserQuery;
import dev.thatwhichis.core.ports.outbound.user.UserRepository;
import dev.thatwhichis.framework.cqrs.QueryHandler;
import dev.thatwhichis.library.error.Error;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ShowUserHandler implements QueryHandler<UserQuery.Show, User> {

    private final UserRepository userRepository;

    @Inject
    public ShowUserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @ConsumeEvent(value = "query.user.show")
    @WithSession
    @WithSpan("handleUserShowQuery")
    public Uni<Result<User, Error>> execute(UserQuery.Show query) {
        var userId = query.userId();

        return userRepository.get(userId);
    }
}
