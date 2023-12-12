package space.nanobreaker.configuration.microservice.user.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import lombok.Data;
import space.nanobreaker.configuration.microservice.user.dto.user.validation.PasswordsMatch;
import space.nanobreaker.core.usecases.v1.user.command.RegisterUserCommand;

@Data
@PasswordsMatch(message = "passwords should match")
public class RegistrationRequestTO {

    @FormParam("username")
    @NotBlank(message = "username can not be blank")
    String username;

    @FormParam("password")
    @NotBlank(message = "password can not be blank")
    String password;
    @FormParam("repeat-password")
    @NotBlank(message = "repeat password can not be blank")
    String repeatPassword;

    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(username, password);
    }
}