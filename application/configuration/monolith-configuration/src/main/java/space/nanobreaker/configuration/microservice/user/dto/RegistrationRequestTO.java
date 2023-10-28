package space.nanobreaker.configuration.microservice.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import space.nanobreaker.configuration.microservice.user.dto.validation.PasswordsMatch;
import space.nanobreaker.core.usecases.v1.user.RegisterUserUseCase;

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

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public RegisterUserUseCase.RegisterUserUseCaseRequest createRequest() {
        return new RegisterUserUseCase.RegisterUserUseCaseRequest(username, password);
    }

}