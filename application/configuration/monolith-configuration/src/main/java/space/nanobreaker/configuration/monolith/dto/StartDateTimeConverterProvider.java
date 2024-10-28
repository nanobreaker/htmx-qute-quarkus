package space.nanobreaker.configuration.monolith.dto;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import space.nanobreaker.configuration.monolith.services.command.StartDateTime;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class StartDateTimeConverterProvider implements ParamConverterProvider {

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(
            Class<T> rawType,
            Type genericType,
            Annotation[] annotations
    ) {
        if (rawType.equals(StartDateTime.class)) {
            return (ParamConverter<T>) new StartDateTimeConverter();
        }
        return null;
    }
}
