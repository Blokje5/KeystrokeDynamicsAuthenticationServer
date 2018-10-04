package identity.login.controller;

import com.google.gson.Gson;
import com.google.inject.Inject;
import identity.login.service.LoginService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginController {

    private LoginService loginService;

    @Inject public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    public Route handlePostLogin = (Request request, Response response) -> {
        LoginRequest loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);
        return loginService.validateLoginRequest(loginRequest);
    };
}
