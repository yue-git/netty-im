package own.yue.im.protocol.payload.dto.server2client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import own.yue.im.protocol.payload.Server2ClientPayload;

@Getter
@Setter
@NoArgsConstructor
public class MessageDeliver implements Server2ClientPayload {
    private String fromUserId;
    private String msg;

    public MessageDeliver(String fromUserId, String msg) {
        this.fromUserId = fromUserId;
        this.msg = msg;
    }
}
