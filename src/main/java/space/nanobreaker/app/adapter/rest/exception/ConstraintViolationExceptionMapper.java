package space.nanobreaker.app.adapter.rest.exception;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Location("v1/exception/violation.qute.html")
    Template violationTemplate;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        return Response.serverError()
                .entity(mapErrorToTemplate(exception))
                .build();
    }

    private TemplateInstance mapErrorToTemplate(ConstraintViolationException exception) {
        List<ViolationDescription> violations = exception.getConstraintViolations().stream()
                .map(violation -> new ViolationDescription(String.format("Field[%s]", violation.getPropertyPath().toString()), violation.getMessage()))
                .toList();

        return violationTemplate.data("violations", violations);
    }

    record ViolationDescription(String propertyPath, String message) {
    }

}