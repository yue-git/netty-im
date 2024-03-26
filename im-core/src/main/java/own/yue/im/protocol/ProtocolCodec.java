package own.yue.im.protocol;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import own.yue.im.extension.serial.SerialCache;
import own.yue.im.protocol.payload.ProtocolPayload;
import own.yue.im.util.ImServiceHandlerRegister;

import javax.annotation.Resource;


/**
 * TODO 序列化以及压缩拓展待实现
 */
@Component
@Slf4j
public class ProtocolCodec {
    @Resource
    private ImServiceHandlerRegister imServiceHandlerRegister;

    public Packet decode(ByteBuf byteBuf) {
        byteBuf.skipBytes(4);
        Packet packet = Packet.build()
                .version(byteBuf.readByte())
                .msgId(byteBuf.readInt())
                .msgType(byteBuf.readShort())
                .serial(byteBuf.readByte())
                .compress(byteBuf.readByte()).build();
        int payloadLen = byteBuf.readInt();
        packet.setMsgLen(payloadLen);
        byte[] bytes = new byte[payloadLen];
        byteBuf.readBytes(bytes);

        @SuppressWarnings(value = {"unchecked"})
        ProtocolPayload payload = SerialCache.getInstance(packet.getSerial()).deserialize(bytes, imServiceHandlerRegister.getHandler(packet.getMsgType()).getInboundMsgTypeClass());
        packet.setPayload(payload);

        return packet;
    }

    public void encode(ByteBuf byteBuf, Packet packet) {
        byteBuf.writeInt(ProtocolConstants.MAGIC_NUMBER_INT);
        byteBuf.writeByte(ProtocolConstants.PROTOCOL_VERSION.VERSION_1);
        byteBuf.writeInt(1);
        byteBuf.writeShort(packet.getMsgType());
//        TODO 这里序列化与压缩应该按照客户端偏好来，因为物联网场景下客户端可能是单片机等无法支持复杂序列化或压缩的简单机器
        byteBuf.writeByte(ProtocolConstants.Serial.KRYO);
        byteBuf.writeByte(ProtocolConstants.Compress.NONE.getBitValue());

        byte[] encode = SerialCache.getInstance(packet.getSerial()).serialize(packet.getPayload());
        byteBuf.writeInt(encode.length);
        byteBuf.writeBytes(encode);
    }
}
