package own.yue.im.protocol.payload.dto.client2server;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import own.yue.im.protocol.payload.Client2ServerPayload;
import own.yue.im.protocol.payload.Server2ClientPayload;

@Getter
@Setter
@NoArgsConstructor
public class MessageSendToUser implements Client2ServerPayload {
    private String toUserId;
    private String msg;

    public MessageSendToUser(String toUserId, String msg) {
        this.toUserId = toUserId;
        this.msg = msg;
    }
}
