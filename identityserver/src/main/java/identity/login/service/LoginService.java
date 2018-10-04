package identity.login.service;

import java.util.Optional;
import javax.inject.Inject;

import data.CalculateFeatureAlgorithms;
import data.domain.SampleAggregate;
import data.service.TemplateCacheService;
import exceptions.InvalidRequestException;
import identity.login.auth.TokenProvider;
import identity.login.controller.LoginRequest;
import identity.registration.domain.User;
import identity.registration.service.UserService;
import spark.utils.StringUtils;

public class LoginService {
    private UserService userService;
    private TokenProvider tokenProvider;
    private TemplateCacheService templateCacheService;

    @Inject public LoginService(final UserService userService, final TokenProvider tokenProvider, final TemplateCacheService templateCacheService) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.templateCacheService = templateCacheService;
    }

    public String validateLoginRequest(final LoginRequest loginRequest) {
        final String username = loginRequest.getUsername();
        if (StringUtils.isEmpty(username)) {
            throw new InvalidRequestException("The username or password is wrong");
        }

        final Optional<User> userOptional = Optional.ofNullable(userService.findByUsername(username));
        final User user = userOptional.orElseThrow(() -> new InvalidRequestException("The username or password is wrong"));
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new InvalidRequestException("The username or password is wrong");
        }

        final SampleAggregate userTemplate = templateCacheService.get(username);
        final boolean authenticated = CalculateFeatureAlgorithms.distanceMeasureWithinThreshold(loginRequest.getKeystrokeEvents(), userTemplate);

        if(!authenticated) {
            throw new InvalidRequestException("User's typing patterns do not match template");
        }

        return "{ \"token\": \""  + tokenProvider.provideToken(user) + "\" }";
    }
}
