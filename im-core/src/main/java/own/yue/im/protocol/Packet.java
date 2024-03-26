package own.yue.im.protocol;

import lombok.Getter;
import lombok.Setter;
import own.yue.im.protocol.payload.ProtocolPayload;

@Getter
@Setter
public class Packet {
    private byte version;
    private int msgId;
    private short msgType;
    private byte serial;
    private byte compress;
    private int msgLen;
    private ProtocolPayload payload;

    public Packet(byte version, int msgId, short msgType, byte serial, byte compress, int msgLen, ProtocolPayload payload) {
        this.version = version;
        this.msgId = msgId;
        this.msgType = msgType;
        this.serial = serial;
        this.compress = compress;
        this.msgLen = msgLen;
        this.payload = payload;
    }

    public static Builder build(){
        return new Builder();
    }

    public static final class Builder {
        private byte version = ProtocolConstants.PROTOCOL_VERSION.VERSION_1;
        private int msgId;
        private short msgType;
        private byte serial;
        private byte compress;
        private int msgLen;
        private ProtocolPayload payload;

        private Builder(){}
        public Builder version(byte version) {
            this.version = version;
            return this;
        }

        public Builder msgId(int msgId) {
            this.msgId = msgId;
            return this;
        }

        public Builder msgType(short msgType) {
            this.msgType = msgType;
            return this;
        }

        public Builder serial(byte serial) {
            this.serial = serial;
            return this;
        }

        public Builder compress(byte compress) {
            this.compress = compress;
            return this;
        }

        public Builder msgLen(int msgLen) {
            this.msgLen = msgLen;
            return this;
        }

        public Builder payload(ProtocolPayload payload) {
            this.payload = payload;
            return this;
        }

        public Packet build() {
            return new Packet(version, msgId, msgType, serial, compress, msgLen, payload);
        }
    }
}
