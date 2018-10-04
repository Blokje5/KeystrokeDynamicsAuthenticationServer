package identity.login.auth;

import identity.registration.domain.User;

public interface TokenProvider {
    String provideToken(User user);
}
