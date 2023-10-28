package space.nanobreaker.configuration.microservice.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import space.nanobreaker.core.usecases.v1.user.AuthenticateUserUseCase;

public class AuthenticationRequestTO {

    @FormParam("username")
    @NotBlank(message = "username can not be blank")
    String username;

    @FormParam("password")
    @NotBlank(message = "password can not be blank")
    String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthenticateUserUseCase.AuthenticateUserUseCaseRequest createRequest() {
        return new AuthenticateUserUseCase.AuthenticateUserUseCaseRequest(username, password);
    }
}
