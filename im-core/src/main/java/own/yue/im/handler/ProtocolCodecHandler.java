package own.yue.im.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.springframework.stereotype.Component;
import own.yue.im.protocol.Packet;
import own.yue.im.protocol.ProtocolCodec;
import own.yue.im.protocol.ProtocolConstants;
import own.yue.im.protocol.payload.Server2ClientPayload;

import javax.annotation.Resource;
import java.util.List;

@Component
@ChannelHandler.Sharable
public class ProtocolCodecHandler extends MessageToMessageCodec<ByteBuf, Packet> {
    @Resource
    private ProtocolCodec protocolCodec;

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, List<Object> out) throws Exception {
        ByteBuf byteBuf = ctx.channel().alloc().ioBuffer();
        protocolCodec.encode(byteBuf, packet);
        out.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        out.add(protocolCodec.decode(byteBuf));
    }
}
