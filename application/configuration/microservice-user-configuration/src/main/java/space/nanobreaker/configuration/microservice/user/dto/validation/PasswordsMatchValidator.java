package space.nanobreaker.configuration.microservice.user.dto.validation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import space.nanobreaker.configuration.microservice.user.dto.RegistrationRequest;

@ApplicationScoped
public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, RegistrationRequest> {

    @Override
    public boolean isValid(final RegistrationRequest registrationRequest, final ConstraintValidatorContext context) {
        return registrationRequest.getPassword().equals(registrationRequest.getRepeatPassword());
    }
}