package space.nanobreaker.configuration.monolith.dto;

import jakarta.ws.rs.ext.ParamConverter;
import space.nanobreaker.configuration.monolith.services.command.StartDateTime;
import space.nanobreaker.configuration.monolith.services.parser.Parser;

public class StartDateTimeConverter implements ParamConverter<StartDateTime> {

    @Override
    public StartDateTime fromString(String value) {
        if (value == null) {
            return null;
        }

        final var tuple = Parser.parseString(value);

        return Parser.getStartDateTime(tuple).orThrow();
    }

    @Override
    public String toString(StartDateTime value) {
        if (value == null) {
            return null;
        }

        return value.toDateTime().toString();
    }
}
