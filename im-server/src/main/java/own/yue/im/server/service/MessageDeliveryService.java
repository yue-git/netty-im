package own.yue.im.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import own.yue.im.annotation.ImService;
import own.yue.im.annotation.ImServiceHandler;
import own.yue.im.protocol.ProtocolConstants;
import own.yue.im.protocol.ReqContext;
import own.yue.im.protocol.payload.NoReplyPayload;
import own.yue.im.protocol.payload.dto.client2server.MessageSendToUser;
import own.yue.im.protocol.payload.dto.server2client.MessageDeliver;
import own.yue.im.util.session.SessionUtil;

@Component
@Slf4j
@ImService
public class MessageDeliveryService {
    @ImServiceHandler(inboundMsgType = ProtocolConstants.MESSAGE_TYPE.MESSAGE_SEND_TO_USER, outboundMsgType = ProtocolConstants.MESSAGE_TYPE.NO_REPLY)
    public NoReplyPayload messageToUser(ReqContext reqContext, MessageSendToUser messageSendToUser) {
        log.info("触发 messageToUser");
        if (!reqContext.hasLogin()) {
            throw new RuntimeException("尚未登录");
        }
        String toUserId = messageSendToUser.getToUserId();
        String msg = messageSendToUser.getMsg();
        SessionUtil.writeAndFlush(toUserId, new MessageDeliver(reqContext.getUserId(), msg));
        return NoReplyPayload.INSTANCE;
    }
}
