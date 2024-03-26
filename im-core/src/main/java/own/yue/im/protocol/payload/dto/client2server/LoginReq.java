package own.yue.im.protocol.payload.dto.client2server;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import own.yue.im.protocol.payload.Client2ServerPayload;
@NoArgsConstructor
@Getter
@Setter
public class LoginReq implements Client2ServerPayload {
    private String login;

    public LoginReq(String login) {
        this.login = login;
    }
}
