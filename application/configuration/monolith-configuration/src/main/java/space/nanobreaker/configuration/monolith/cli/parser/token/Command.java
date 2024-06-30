package space.nanobreaker.configuration.monolith.cli.parser.token;

public sealed interface Command extends Token {
    record Create() implements Command {
    }

    record List() implements Command {
    }

    record Update() implements Command {
    }

    record Delete() implements Command {
    }

    record Show() implements Command {
    }
}
