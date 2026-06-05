package dev.thatwhichis.rest.adapter.cli.tokenizer;

import io.github.dcadea.jresult.Result;

import static io.github.dcadea.jresult.Result.err;
import static io.github.dcadea.jresult.Result.ok;

public enum KEYWORD {
    ALL,
    CALENDAR,
    CREATE,
    DELETE,
    HELP,
    LIST,
    LOGOUT,
    SHOW,
    TODO,
    UPDATE,
    USER;

    public static Result<KEYWORD, TokenizerError> from(final String string) {
        return switch (string) {
            case "all" -> ok(ALL);
            case "calendar" -> ok(CALENDAR);
            case "create" -> ok(CREATE);
            case "delete" -> ok(DELETE);
            case "help" -> ok(HELP);
            case "list" -> ok(LIST);
            case "logout" -> ok(LOGOUT);
            case "show" -> ok(SHOW);
            case "todo" -> ok(TODO);
            case "update" -> ok(UPDATE);
            case "user" -> ok(USER);
            default -> err(new TokenizerError.UnknownKeyword(string));
        };
    }
}