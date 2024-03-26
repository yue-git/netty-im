package own.yue.im.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import own.yue.im.protocol.Packet;
import own.yue.im.protocol.ProtocolConstants;
import own.yue.im.protocol.ReqContext;
import own.yue.im.protocol.payload.NoReplyPayload;
import own.yue.im.protocol.payload.Server2ClientPayload;
import own.yue.im.util.ImServiceHandlerRegister;
import own.yue.im.util.session.Session;
import own.yue.im.util.session.SessionUtil;

import javax.annotation.Resource;

/**
 * 直接复制的 ServerCommonHandler 的代码
 * 因为重点是server
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class ClientCommonHandler extends SimpleChannelInboundHandler<Packet> {

    @Resource
    private ImServiceHandlerRegister imServiceHandlerRegister;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet inBoundPacket) throws Exception {


        ImServiceHandlerRegister.RegisterHandler handler = imServiceHandlerRegister.getHandler(inBoundPacket.getMsgType());
        Server2ClientPayload server2ClientPayload;
        try {
//
            Channel channel = ctx.channel();
            Session session = channel.attr(SessionUtil.ATTRIBUTE_SESSION).get();
            if (session == null) {
                session = new Session();
            }
            ReqContext reqContext = new ReqContext(session, channel);
            server2ClientPayload = (Server2ClientPayload) handler.getMethodHandle().invoke(reqContext, inBoundPacket.getPayload());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        if(server2ClientPayload instanceof NoReplyPayload){
            return;
        }

        Packet outBuoudPacket = Packet.build()
                .version(ProtocolConstants.PROTOCOL_VERSION.VERSION_1)
                .msgId(1)
                .msgType(handler.getOutboundMsgType())
                .serial(ProtocolConstants.Serial.KRYO)
                .compress(ProtocolConstants.Compress.NONE.getBitValue())
                .payload(server2ClientPayload).build();

        ctx.writeAndFlush(outBuoudPacket);
    }
}
