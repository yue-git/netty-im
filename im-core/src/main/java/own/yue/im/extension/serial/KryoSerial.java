package own.yue.im.extension.serial;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import own.yue.im.protocol.ProtocolConstants;
import own.yue.im.protocol.payload.ProtocolPayload;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@Conditional(SerialLoadConditional.class)
public class KryoSerial implements Serial {
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @PostConstruct
    public void init(){
        SerialCache.register(ProtocolConstants.Serial.KRYO, this);
    }

    @Override
    public byte[] serialize(ProtocolPayload payload) {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Output output = new Output(byteArrayOutputStream)
        ) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, payload);
            return output.toBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends ProtocolPayload> T deserialize(byte[] bytes, Class<T> tClass) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, tClass);
            return tClass.cast(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
