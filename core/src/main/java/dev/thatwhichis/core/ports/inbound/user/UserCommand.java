package dev.thatwhichis.core.ports.inbound.user;

import dev.thatwhichis.framework.cqrs.Command;

import java.time.Instant;
import java.util.UUID;

public interface UserCommand extends Command {

    record Authenticate(
            UUID id,
            String username,
            String session,
            Instant time
    ) implements UserCommand {

    }
}
