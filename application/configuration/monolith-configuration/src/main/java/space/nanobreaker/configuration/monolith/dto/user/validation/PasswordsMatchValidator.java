package space.nanobreaker.configuration.monolith.dto.user.validation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import space.nanobreaker.configuration.monolith.dto.user.RegistrationRequestTO;

@ApplicationScoped
public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, RegistrationRequestTO> {

    @Override
    public boolean isValid(final RegistrationRequestTO registrationRequestTO, final ConstraintValidatorContext context) {
        return registrationRequestTO.getPassword().equals(registrationRequestTO.getRepeatPassword());
    }
}