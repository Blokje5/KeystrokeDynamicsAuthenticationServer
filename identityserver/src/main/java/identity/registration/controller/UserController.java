package identity.registration.controller;

import javax.inject.Inject;

import com.google.gson.Gson;
import exceptions.InvalidRequestException;
import identity.registration.domain.RegistrationRequest;
import identity.registration.domain.User;
import identity.registration.service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class UserController {
    private UserService userService;

    @Inject  public UserController(UserService userService) {
        this.userService = userService;
    }

    public Route handleRegistrationRequest = (Request request, Response response) -> {
        RegistrationRequest registrationRequest = new Gson().fromJson(request.body(), RegistrationRequest.class);
        if (!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())) {
            throw new InvalidRequestException("Password and confirm are not equal");
        }
        final User user = new User(registrationRequest.getUsername(), registrationRequest.getPassword());
        userService.add(user);

        return new Gson().toJson(user);
    };
}
