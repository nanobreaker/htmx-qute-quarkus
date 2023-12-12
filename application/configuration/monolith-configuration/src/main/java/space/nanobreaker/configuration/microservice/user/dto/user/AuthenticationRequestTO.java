package space.nanobreaker.configuration.microservice.user.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import lombok.Data;
import space.nanobreaker.core.usecases.v1.user.command.AuthenticateUserCommand;

@Data
public class AuthenticationRequestTO {

    @FormParam("username")
    @NotBlank(message = "username can not be blank")
    String username;

    @FormParam("password")
    @NotBlank(message = "password can not be blank")
    String password;

    public AuthenticateUserCommand toCommand() {
        return new AuthenticateUserCommand(username, password);
    }
}
