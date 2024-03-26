package own.yue.im.util.session;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import own.yue.im.protocol.Packet;
import own.yue.im.protocol.ProtocolConstants;
import own.yue.im.protocol.payload.Server2ClientPayload;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SessionUtil {
    public static final String SESSION_KEY = "session";
    public static final AttributeKey<Session> ATTRIBUTE_SESSION = AttributeKey.newInstance(SESSION_KEY);
    private static final Map<String, Channel> userIdChannelMap = new ConcurrentHashMap<>();

    public static boolean bindSession(Session session, Channel channel) {
        boolean b = channel.attr(SessionUtil.ATTRIBUTE_SESSION).compareAndSet(null, session);
        if (b) {
            userIdChannelMap.put(session.getUserId(), channel);
        }
        return b;
    }

    public static void unBindSession(Channel channel) {
        Session session = channel.attr(ATTRIBUTE_SESSION).get();
        if (session != null && session.isLogin()) {
            userIdChannelMap.remove(session.getUserId());
            channel.attr(ATTRIBUTE_SESSION).set(null);
            if (log.isDebugEnabled()) {
                log.debug("{} 退出", session.getUserId());
            }
        }
    }

    public static boolean writeAndFlush(String uId, Server2ClientPayload payload){
        Channel channel = userIdChannelMap.get(uId);
        if(channel == null){
         return false;
        }
        Packet outBuoudPacket = Packet.build()
                .version(ProtocolConstants.PROTOCOL_VERSION.VERSION_1)
                .msgId(1)
                .msgType(ProtocolConstants.MESSAGE_TYPE.DELIVER_MESSAGE)
                .serial(ProtocolConstants.Serial.KRYO)
                .compress(ProtocolConstants.Compress.NONE.getBitValue())
                .payload(payload).build();
        channel.writeAndFlush(outBuoudPacket);
        return true;
    }



}
