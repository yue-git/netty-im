package own.yue.im.protocol;

import lombok.Getter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * custom protocol decoder
 * <p>
 * <pre>
 *   0        1        2        3        4        5        6        7        8        9        10       11       12       13       14       15        16
 *   +--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------|
 *   |           magic   code            |version |               MsgId               |     msgType     | serial |compress|           message-length          |
 *   +--------------------------------------------------------------------------------------------------------------------------------------------------------+
 *   |                                                                     ... body ...                                                                       |
 *   +--------------------------------------------------------------------------------------------------------------------------------------------------------+
 *   4B magic code（魔法数）    1B version（版本）    4B requestId（请求的Id）    2B messageType（消息类型）   1B serial（序列化类型）  1B compress（压缩类型）     4B full length（消息长度）      body（数据字节流）
 *
 * </pre>
 */
public class ProtocolConstants {
    public static final int MAGIC_NUMBER_INT = 0xabbabcde;
    public static final byte[] MAGIC_NUMBER_BYTES = {
            (byte) (MAGIC_NUMBER_INT >>> 24),
            (byte) (MAGIC_NUMBER_INT >>> 16),
            (byte) (MAGIC_NUMBER_INT >>> 8),
            (byte) MAGIC_NUMBER_INT};
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * 协议版本，预留的升级空间
     */
    public static final class PROTOCOL_VERSION {
        /**
         * 2024年3月15日
         */
        public static final byte VERSION_1 = 1;
    }

    /**
     * 消息类型，根据业务而定
     */
    public static class MESSAGE_TYPE {
        public static final short PING = -1;
        public static final short PONG = -2;
        public static final short NO_REPLY = 0;
        public static final short LOGIN_REQ = 1;
        public static final short LOGIN_RES = 2;
        public static final short MESSAGE_SEND_TO_USER = 3;
        public static final short DELIVER_MESSAGE = 4;
    }

    /**
     * 序列号协议
     */
    @Getter
    public static class Serial {
        public static final byte KRYO = (byte) 1;
        public static final byte PROTOBUF = (byte) 2;
        public static final byte PROTOSTUFF = (byte) 3;
        public static final byte HESSIAN = (byte) 4;
        public static final byte JSON = (byte) 5;
        private final Byte bitValue;

        Serial(Byte bitValue) {
            this.bitValue = bitValue;
        }
    }

    /**
     * 消息压缩算法
     */
    @Getter
    public static enum Compress {
        NONE((byte) 0),
        GZIP((byte) 1),
        SNAPPY((byte) 2),
        LZ4((byte) 3),
        Z_STANDARD((byte) 4),
        ;
        private final Byte bitValue;

        Compress(Byte bitValue) {
            this.bitValue = bitValue;
        }

        private static final Map<Byte, Compress> ID_MAP_NAME = new HashMap<>();

        static {
            for (Compress serial : Compress.values()) {
                ID_MAP_NAME.put(serial.bitValue, serial);
            }
        }

        public static Compress getName(Byte bitValue) {
            return ID_MAP_NAME.get(bitValue);
        }
    }

    public static final int MAX_DATA_LENGTH = 1024 * 1024;

    public static final int LENGTH_FIELD_OFFSET = MAGIC_NUMBER_BYTES.length + 1 + 4 + 2 + 1 + 1;
    public static final int MAX_FRAME_LENGTH = LENGTH_FIELD_OFFSET + 4 + MAX_DATA_LENGTH;

}
