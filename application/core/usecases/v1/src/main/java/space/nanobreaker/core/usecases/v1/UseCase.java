package space.nanobreaker.core.usecases.v1;

import io.smallrye.mutiny.Uni;

public interface UseCase<REQ extends Request, RES extends Response> {

    Uni<RES> execute(REQ request);

}
