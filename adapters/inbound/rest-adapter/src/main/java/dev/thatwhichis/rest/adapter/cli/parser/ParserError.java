package dev.thatwhichis.rest.adapter.cli.parser;

import dev.thatwhichis.library.error.Error;

public sealed interface ParserError extends Error {

    // @formatter:off
    record UnknownToken(String program)                 implements ParserError { }
    record NotProgram(String keyword)                   implements ParserError { }
    record NotKeyword(String value)                     implements ParserError { }
    record UnknownCommand(String command)               implements ParserError { }
    record ArgumentNotFound()                           implements ParserError { }
    record ArgumentOrFilterNotFound()                   implements ParserError { }
    record RedundantArgs()                              implements ParserError { }
    record NotSupportedOperation()                      implements ParserError { }
    record DateTimeParseError(String description)       implements ParserError { }
    record DateParseError(String description)           implements ParserError { }
    record DateTimeParseException(Exception exception)  implements ParserError { }
    record TimeParseError(String description)           implements ParserError { }
    record EmptyDateTime()                              implements ParserError { }
    record EmptyDate()                                  implements ParserError { }
    record EmptyTime()                                  implements ParserError { }
    record Empty()                                      implements ParserError { }
    record Unreachable()                                implements ParserError { }
    record Uncategorized(Exception exception)           implements ParserError { }

    @Override
    default String describe() {
        return switch (this) {
            case RedundantArgs              _ ->    "parser error: only one argument is allowed";
            case ArgumentNotFound           _ ->    "parser error: argument required";
            case ArgumentOrFilterNotFound   _ ->    "parser error: argument or filter required";
            case NotSupportedOperation      _ ->    "parser error: not supported operation";
            case UnknownToken               e ->    "parser error: program \"%s\" not supported"
                                                    .formatted(e.program());
            case NotProgram                 e ->    "parser error: provided keyword \"%s\" is not a program"
                                                    .formatted(e.keyword());
            case NotKeyword                 e ->    "parser error: \"%s\" is not a valid keyword"
                                                    .formatted(e.value());
            case UnknownCommand             e ->    "parser error: command \"%s\" not supported"
                                                    .formatted(e.command());
            case DateTimeParseError         e ->    "parser error: string \"%s\" is not a valid date issuedAt"
                                                    .formatted(e.description());
            case DateParseError             e ->    "parser error: string \"%s\" is not a valid date"
                                                    .formatted(e.description());
            case TimeParseError             e ->    "parser error: string \"%s\" is not a valid date"
                                                    .formatted(e.description());
            case DateTimeParseException     e ->    "parser error: string \"%s\" is not a valid date"
                                                    .formatted(e.exception().getMessage());
            case Empty                      _,
                 EmptyDateTime              _,
                 EmptyDate                  _,
                 EmptyTime                  _ ->    "parser error: empty input";
            case Unreachable                _ ->    "parser error: unreachable branch reached";
            case Uncategorized              e ->    "parser error: uncategorized error: %s"
                                                    .formatted(e.exception().getMessage());
        };
    }
    // @formatter:on
}