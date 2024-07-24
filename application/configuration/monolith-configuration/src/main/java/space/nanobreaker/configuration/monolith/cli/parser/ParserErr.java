package space.nanobreaker.configuration.monolith.cli.parser;

import space.nanobreaker.library.Error;

public sealed interface ParserErr extends Error {

    // @formatter:off
    record UnknownProgram() implements ParserErr { }
    record UnknownCommand() implements ParserErr { }
    record ArgumentNotFound() implements ParserErr { }
    record NotSupportedOperation() implements ParserErr { }
    record DateTimeParseErr(String description) implements ParserErr { }
    // @formatter:on

}