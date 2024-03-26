package own.yue.im.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import own.yue.im.annotation.ImService;
import own.yue.im.annotation.ImServiceHandler;
import own.yue.im.protocol.ProtocolConstants;
import own.yue.im.protocol.ReqContext;
import own.yue.im.protocol.payload.NoReplyPayload;
import own.yue.im.protocol.payload.dto.server2client.MessageDeliver;
import own.yue.im.util.session.Session;

@Slf4j
@Component
@ImService
public class ReceiveMessageService {
    @ImServiceHandler(inboundMsgType = ProtocolConstants.MESSAGE_TYPE.DELIVER_MESSAGE, outboundMsgType = ProtocolConstants.MESSAGE_TYPE.NO_REPLY)
    public NoReplyPayload receiveMessage(ReqContext reqContext, MessageDeliver messageDeliver) {
        log.info("收到来自：{} 的一条消息，内容：{}", messageDeliver.getFromUserId(), messageDeliver.getMsg());
        return NoReplyPayload.INSTANCE;
    }
}
