package space.nanobreaker.core.usecases.v1.user.command;

import space.nanobreaker.core.usecases.v1.Command;

public record AuthenticateUserCommand(String username, String password) implements Command {
}
