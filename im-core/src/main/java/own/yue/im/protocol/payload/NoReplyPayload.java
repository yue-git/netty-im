package own.yue.im.protocol.payload;

/**
 * 无需回复
 */
public class NoReplyPayload implements Server2ClientPayload, Client2ServerPayload{
    private NoReplyPayload(){}
    public static final NoReplyPayload INSTANCE = new NoReplyPayload();
}
