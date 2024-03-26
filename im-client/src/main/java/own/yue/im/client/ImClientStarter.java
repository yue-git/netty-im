package own.yue.im.client;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import own.yue.im.annotation.ImService;
import own.yue.im.util.ImServiceHandlerRegister;

import java.util.Map;
import java.util.Scanner;

@ComponentScan(basePackages = "own.yue.im")
@Slf4j
public class ImClientStarter {


    public static void main(String[] args) throws InterruptedException, NoSuchMethodException, IllegalAccessException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImClientStarter.class);

        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(ImService.class);
        context.getBean(ImServiceHandlerRegister.class).register(beansWithAnnotation.values());

        NettyClient nettyClient = context.getBean(NettyClient.class);
        nettyClient.connect();


        Scanner scanner = new Scanner(System.in);
        while (true) {
            String s = scanner.nextLine();
            log.info("收到输入：{}", s);
            if ("exit".equals(s)) {
                System.exit(0);
            }
            nettyClient.sendMessage(s);
        }
    }


}
