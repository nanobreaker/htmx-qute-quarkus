package space.nanobreaker.configuration.monolith.services.parser;

import space.nanobreaker.library.error.Error;

public sealed interface ParserError extends Error {

    // @formatter:off
    record UnknownProgram()                         implements ParserError { }
    record UnknownCommand()                         implements ParserError { }
    record ArgumentNotFound()                       implements ParserError { }
    record NotSupportedOperation()                  implements ParserError { }
    record DateTimeParseError(String description)   implements ParserError { }
    record DateParseError(String description)       implements ParserError { }
    record TimeParseError(String description)       implements ParserError { }
    record EmptyDateTime()                          implements ParserError { }
    record EmptyDate()                              implements ParserError { }
    record EmptyTime()                              implements ParserError { }
    // @formatter:on

    @Override
    default String describe() {
        return switch (this) {
            case ArgumentNotFound _ -> "parser error: argument required";
            case NotSupportedOperation _ -> "parser error: not supported operation";
            case UnknownCommand _ -> "parser error: unknown command";
            case UnknownProgram _ -> "parser error: unknown program";
            case DateTimeParseError e ->
                "parser error: string \"%s\" is not a valid date time".formatted(e.description());
            case DateParseError e ->
                "parser error: string \"%s\" is not a valid date".formatted(e.description());
            case TimeParseError e ->
                "parser error: string \"%s\" is not a valid date".formatted(e.description());
            case EmptyDateTime _,
                 EmptyDate _,
                 EmptyTime _ -> "<ignored>";
        };
    }
}