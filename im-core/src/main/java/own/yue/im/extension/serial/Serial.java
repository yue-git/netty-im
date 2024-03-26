package own.yue.im.extension.serial;

import own.yue.im.protocol.payload.ProtocolPayload;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public interface Serial {
    byte[] serialize(ProtocolPayload payload);

    <T extends ProtocolPayload> T deserialize(byte[] bytes, Class<T> tClass);


}
