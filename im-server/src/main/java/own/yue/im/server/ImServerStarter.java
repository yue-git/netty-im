package own.yue.im.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import own.yue.im.annotation.ImService;
import own.yue.im.annotation.ImServiceHandler;
import own.yue.im.util.ImServiceHandlerRegister;

import java.util.Map;


@ComponentScan(basePackages = "own.yue.im")
public class ImServerStarter {
    public static final Logger log = LoggerFactory.getLogger(ImServerStarter.class);

    public static void main(String[] args) throws InterruptedException, NoSuchMethodException, IllegalAccessException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImServerStarter.class);

        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(ImService.class);
        context.getBean(ImServiceHandlerRegister.class).register(beansWithAnnotation.values());

        NettyServer nettyServer = context.getBean(NettyServer.class);
        nettyServer.start();

    }
}
