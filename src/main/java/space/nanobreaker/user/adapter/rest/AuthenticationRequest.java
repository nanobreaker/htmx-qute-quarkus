package space.nanobreaker.user.adapter.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;

public class AuthenticationRequest {

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
}
