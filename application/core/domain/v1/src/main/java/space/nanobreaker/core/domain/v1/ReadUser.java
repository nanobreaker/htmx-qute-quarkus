package space.nanobreaker.core.domain.v1;

import java.util.UUID;

public record ReadUser(
        UUID id,
        String username
) {

}