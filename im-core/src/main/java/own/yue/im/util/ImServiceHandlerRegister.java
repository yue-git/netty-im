package own.yue.im.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import own.yue.im.annotation.ImServiceHandler;
import own.yue.im.protocol.ReqContext;
import own.yue.im.protocol.payload.Client2ServerPayload;
import own.yue.im.protocol.payload.ProtocolPayload;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class ImServiceHandlerRegister {
    private final ConcurrentMap<Short, RegisterHandler> serviceHandlerMap = new ConcurrentHashMap<>();

    public RegisterHandler getHandler(Short messageType) {
        return serviceHandlerMap.get(messageType);
    }

    /**
     * 通过注解过滤得到业务逻辑方法，然后在此对业务逻辑与协议的messageType进行映射，以实现动态调用。
     * 这样就可以专心关注业务逻辑了
     * @param imServices
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     */
    public void register(Collection<Object> imServices) throws NoSuchMethodException, IllegalAccessException {
        for (Object imService : imServices) {
            for (Method declaredMethod : imService.getClass().getMethods()) {
                if (declaredMethod.isAnnotationPresent(ImServiceHandler.class)) {
                    Class inboundMsgTypeClass = checkMethodParam(declaredMethod);
                    ImServiceHandler annotation = declaredMethod.getAnnotation(ImServiceHandler.class);
                    MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
                    MethodType methodType = MethodType.methodType(declaredMethod.getReturnType(), new Class[]{ReqContext.class, inboundMsgTypeClass});
                    // 3.通过MethodHandles.Lookup指定方法定义类、方法名称以及MethodType 来查找对应的方法句柄
                    MethodHandle methodHandle = publicLookup.findVirtual(imService.getClass(), declaredMethod.getName(), methodType)
                            .bindTo(imService);

                    if (serviceHandlerMap.containsKey(annotation.inboundMsgType())) {
                        throw new RuntimeException("消息类型：" + annotation.inboundMsgType() + " 处理方式被重复注册，请检查");
                    }
                    serviceHandlerMap.put(
                            annotation.inboundMsgType(),
                            new RegisterHandler(annotation.inboundMsgType(), annotation.outboundMsgType(), inboundMsgTypeClass, declaredMethod.getReturnType(), methodHandle)
                    );
                }
            }
        }
    }

    /**
     * 强制检查参数，第一个参数是 SessionContext，第二个参数是 Client2ServerPayload实现。
     * 可以不做强制，鉴于这只是demo，就在这里怎么方便怎么来
     *
     * @param declaredMethod
     */
    private Class checkMethodParam(Method declaredMethod) {
        Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
        if (parameterTypes.length != 2) {
            throw new RuntimeException("方法：" + declaredMethod.getName() + " 参数过长");
        }
        if (!parameterTypes[0].equals(ReqContext.class)) {
            throw new RuntimeException("方法：" + declaredMethod.getName() + " 第一个参数应该是SessionContext");
        }
        if (!(ProtocolPayload.class.isAssignableFrom(parameterTypes[1]))) {
            throw new RuntimeException("方法：" + declaredMethod.getName() + " 第二个参数应该是 ProtocolPayload 的实现");
        }
        return parameterTypes[1];
    }

    @Getter
    public static class RegisterHandler {
        private final short inboundMsgType;
        private final short outboundMsgType;
        private final Class inboundMsgTypeClass;
        private final Class outboundMsgTypeClass;
        private final MethodHandle methodHandle;


        public RegisterHandler(short inboundMsgType, short outboundMsgType, Class inboundMsgTypeClass, Class outboundMsgTypeClass, MethodHandle methodHandle) {
            this.inboundMsgType = inboundMsgType;
            this.outboundMsgType = outboundMsgType;
            this.inboundMsgTypeClass = inboundMsgTypeClass;
            this.outboundMsgTypeClass = outboundMsgTypeClass;
            this.methodHandle = methodHandle;
        }
    }
}
