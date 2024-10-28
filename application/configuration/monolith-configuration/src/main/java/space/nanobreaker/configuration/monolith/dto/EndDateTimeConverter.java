package space.nanobreaker.configuration.monolith.dto;

import jakarta.ws.rs.ext.ParamConverter;
import space.nanobreaker.configuration.monolith.services.command.EndDateTime;
import space.nanobreaker.configuration.monolith.services.parser.Parser;

public class EndDateTimeConverter implements ParamConverter<EndDateTime> {

    @Override
    public EndDateTime fromString(String value) {
        if (value == null) {
            return null;
        }

        final var tuple = Parser.parseString(value);

        return Parser.getEndDateTime(tuple).orThrow();
    }

    @Override
    public String toString(EndDateTime value) {
        if (value == null) {
            return null;
        }

        return value.toDateTime().toString();
    }
}
