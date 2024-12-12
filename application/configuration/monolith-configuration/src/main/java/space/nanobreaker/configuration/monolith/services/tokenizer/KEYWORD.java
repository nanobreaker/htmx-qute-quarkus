package space.nanobreaker.configuration.monolith.services.tokenizer;

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
    ALL,
    UNKNOWN;

    public static KEYWORD from(final String string) {
        return switch (string) {
            case "help" -> HELP;
            case "todo" -> TODO;
            case "user" -> USER;
            case "calendar" -> CALENDAR;
            case "create" -> CREATE;
            case "list" -> LIST;
            case "show" -> SHOW;
            case "update" -> UPDATE;
            case "delete" -> DELETE;
            case "all" -> ALL;
            default -> UNKNOWN;
        };
    }
}