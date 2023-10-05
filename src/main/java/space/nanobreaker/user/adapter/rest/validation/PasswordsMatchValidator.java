package space.nanobreaker.user.adapter.rest.validation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import space.nanobreaker.user.adapter.rest.RegistrationRequest;

@ApplicationScoped
public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, RegistrationRequest> {

    @Override
    public boolean isValid(final RegistrationRequest registrationRequest, final ConstraintValidatorContext context) {
        return registrationRequest.getPassword().equals(registrationRequest.getRepeatPassword());
    }
}