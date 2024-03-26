package own.yue.im.protocol.payload.dto.server2client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import own.yue.im.protocol.payload.Server2ClientPayload;

@NoArgsConstructor
@Getter
@Setter
public class LoginRes implements Server2ClientPayload {
    private boolean loginResult;

    public LoginRes(boolean loginResult) {
        this.loginResult = loginResult;
    }
}
