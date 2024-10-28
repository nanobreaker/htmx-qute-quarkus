package space.nanobreaker.configuration.monolith.dto;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import space.nanobreaker.configuration.monolith.services.command.EndDateTime;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class EndDateTimeConverterProvider implements ParamConverterProvider {

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(
            Class<T> rawType,
            Type genericType,
            Annotation[] annotations
    ) {
        if (rawType.equals(EndDateTime.class)) {
            return (ParamConverter<T>) new EndDateTimeConverter();
        }
        return null;
    }
}
