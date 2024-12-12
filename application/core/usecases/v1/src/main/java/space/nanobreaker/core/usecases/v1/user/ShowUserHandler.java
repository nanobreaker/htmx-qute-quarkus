package space.nanobreaker.core.usecases.v1.user;

import io.github.dcadea.jresult.Result;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.core.domain.v1.Query;
import space.nanobreaker.cqrs.QueryHandler;
import space.nanobreaker.library.error.Error;

import static io.github.dcadea.jresult.Result.empty;

@ApplicationScoped
public class ShowUserHandler implements QueryHandler<Query.User.Show, Void> {

    @Override
    public Uni<Result<Void, Error>> execute(Query.User.Show query) {
        return Uni.createFrom().item(empty());
    }
}
