package identity;

import com.google.inject.AbstractModule;
import identity.login.LoginModule;
import identity.registration.RegistrationModule;

public class IdentityModule extends AbstractModule {
    protected void configure() {
        install(new RegistrationModule());
        install(new LoginModule());
    }
}
