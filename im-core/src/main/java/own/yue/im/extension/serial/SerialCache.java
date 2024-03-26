package own.yue.im.extension.serial;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SerialCache {
    private final static ConcurrentMap<Byte, Serial> CACHE = new ConcurrentHashMap<>();
    public static Serial getInstance(byte serialType) {
        return CACHE.get(serialType);
    }
    public static void register(byte serialType, Serial serial){
        CACHE.put(serialType, serial);
    }
}
