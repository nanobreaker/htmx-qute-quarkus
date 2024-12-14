package space.nanobreaker.configuration.monolith.services.tokenizer;

public enum OPTION {
    TITLE,
    DESCRIPTION,
    START,
    END,
    FILTER,
    UNKNOWN;

    public static OPTION from(final String string) {
        return switch (string) {
            case "t" -> TITLE;
            case "d" -> DESCRIPTION;
            case "s" -> START;
            case "e" -> END;
            case "f" -> FILTER;
            default -> UNKNOWN;
        };
    }
}