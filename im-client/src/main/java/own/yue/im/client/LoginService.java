package own.yue.im.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import own.yue.im.annotation.ImService;
import own.yue.im.annotation.ImServiceHandler;
import own.yue.im.protocol.ProtocolConstants;
import own.yue.im.protocol.ReqContext;
import own.yue.im.protocol.payload.NoReplyPayload;
import own.yue.im.protocol.payload.dto.server2client.LoginRes;
import own.yue.im.util.session.Session;

@Slf4j
@Component
@ImService
public class LoginService {
    @ImServiceHandler(inboundMsgType = ProtocolConstants.MESSAGE_TYPE.LOGIN_RES, outboundMsgType = ProtocolConstants.MESSAGE_TYPE.NO_REPLY)
    public NoReplyPayload loginResHandler(ReqContext reqContext, LoginRes loginRes){
        log.info("执行 loginResHandler， 参数：{}", loginRes.isLoginResult());
        return NoReplyPayload.INSTANCE;
    }
}
