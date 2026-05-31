package dev.thatwhichis.app.usecases.user;

import dev.thatwhichis.core.domain.user.User;
import dev.thatwhichis.core.domain.user.UserSession;
import dev.thatwhichis.core.ports.inbound.user.UserCommand;
import dev.thatwhichis.core.ports.outbound.user.UserRepository;
import dev.thatwhichis.framework.cqrs.CommandHandler;
import dev.thatwhichis.library.error.Error;
import dev.thatwhichis.library.option.None;
import dev.thatwhichis.library.option.Some;
import io.github.dcadea.jresult.Err;
import io.github.dcadea.jresult.Ok;
import io.github.dcadea.jresult.Result;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class UserAuthenticatedHandler implements CommandHandler<UserCommand.Authenticate, Void> {

    private final UserRepository userRepository;

    @Inject
    public UserAuthenticatedHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @ConsumeEvent(value = "user.authenticated")
    @WithSession
    @WithSpan("handleUserAuthenticatedCommand")
    public Uni<Result<Void, Error>> handle(UserCommand.Authenticate command) {
        var id = command.id();
        var userUniResOpt = userRepository.find(id);

        return userUniResOpt.flatMap(result -> switch (result) {
            case Ok(Some(var user)) -> {
                var session = new UserSession(command.sid(), command.issuedAt(), command.expiresAt());

                user.updateSession(session);
                user.setTouchedAt(command.issuedAt());

                yield userRepository
                        .save(user)
                        .replaceWith(Result.empty());
            }
            case Ok(None()) -> {
                var session = new UserSession(command.sid(), command.issuedAt(), command.expiresAt());
                var user = new User.Builder(id)
                        .withUsername(command.upn())
                        .withSessions(new HashSet<>(Set.of(session)))
                        .withCreatedAt(command.issuedAt())
                        .withTouchedAt(command.issuedAt())
                        .build();

                // todo: handle result, in case of error propagate
                yield userRepository
                        .save(user)
                        .replaceWith(Result.empty());
            }
            case Err(var error) -> Uni.createFrom().item(Result.err(error));
        });
    }
}
