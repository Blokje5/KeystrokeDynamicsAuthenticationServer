package identity.registration;

import com.google.inject.AbstractModule;
import identity.registration.service.UserService;

public class RegistrationModule extends AbstractModule {
    protected void configure() {
       bind(UserService.class);
    }
}
