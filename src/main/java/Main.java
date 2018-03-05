import config.ServerConfig;
import http.HttpServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ServerConfig.class);

        HttpServer server = applicationContext.getBean(HttpServer.class);
        server.start();
    }
}
