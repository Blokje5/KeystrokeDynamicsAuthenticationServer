package identity.login.auth;

import java.security.Key;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import identity.registration.domain.User;

public class JWTTokenProvider implements TokenProvider {
    public String provideToken(User user) {
        Key key = MacProvider.generateKey();

        String compactJws = Jwts.builder()
                .setSubject(user.getUsername())
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

        return compactJws;
    }
}
