package space.nanobreaker.auth.adapter.rest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = PasswordsMatchValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface PasswordsMatch {

    String message() default "{space.nanobreaker.UniqueUser.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}