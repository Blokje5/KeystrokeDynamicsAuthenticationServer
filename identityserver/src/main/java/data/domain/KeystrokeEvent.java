package data.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeystrokeEvent {
    private String sessionId;
    private String eventTime;
    private String eventType;
    private String keyName;
    private String keyCode;
    private String inputType;
    private String id;
}
