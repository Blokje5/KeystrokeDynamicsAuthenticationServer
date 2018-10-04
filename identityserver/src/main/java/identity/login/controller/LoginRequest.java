package identity.login.controller;

import java.util.List;

import data.domain.KeystrokeEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
    private List<KeystrokeEvent> keystrokeEvents;
}
