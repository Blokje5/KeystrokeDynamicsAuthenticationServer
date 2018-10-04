package identity.login;

import com.google.inject.AbstractModule;
import identity.login.auth.JWTTokenProvider;
import identity.login.auth.TokenProvider;
import identity.login.service.LoginService;

public class LoginModule extends AbstractModule {
    protected void configure() {
        bind(TokenProvider.class).to(JWTTokenProvider.class);
        bind(LoginService.class);
    }
}
