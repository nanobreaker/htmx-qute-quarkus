package dev.thatwhichis.rest.adapter.parsing.token;

import io.github.dcadea.jresult.Result;

import static io.github.dcadea.jresult.Result.err;
import static io.github.dcadea.jresult.Result.ok;

public enum KEYWORD {
    HELP,
    TODO,
    USER,
    CALENDAR,
    CREATE,
    LIST,
    SHOW,
    UPDATE,
    DELETE,
    ALL;

    public static Result<KEYWORD, TokenizerError> from(final String string) {
        return switch (string) {
            case "help" -> ok(HELP);
            case "todo" -> ok(TODO);
            case "user" -> ok(USER);
            case "calendar" -> ok(CALENDAR);
            case "create" -> ok(CREATE);
            case "list" -> ok(LIST);
            case "show" -> ok(SHOW);
            case "update" -> ok(UPDATE);
            case "delete" -> ok(DELETE);
            case "all" -> ok(ALL);
            default -> err(new TokenizerError.UnknownKeyword(string));
        };
    }
}