package dev.thatwhichis.app.usecases.user;

import dev.thatwhichis.core.ports.inbound.UserQuery;
import dev.thatwhichis.framework.cqrs.QueryHandler;
import dev.thatwhichis.library.error.Error;
import io.github.dcadea.jresult.Result;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import static io.github.dcadea.jresult.Result.empty;

@ApplicationScoped
public class ShowUserHandler implements QueryHandler<UserQuery.Show, Void> {

    @Override
    public Uni<Result<Void, Error>> execute(UserQuery.Show query) {
        return Uni.createFrom().item(empty());
    }
}
