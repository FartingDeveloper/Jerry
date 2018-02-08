package config;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import servlet.JerryServletContext;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
@Import(ContextConfig.class)
@PropertySource("classpath:/com/rg/server.properties")
public class ServerConfig {

    @Value("${port}")
    private int port;

    @Bean(destroyMethod = "close")
    public ServerSocket serverSocket() throws IOException {
        return new ServerSocket(port);
    }

    @Bean
    public Map<String, HttpRequestHandler> handlers(Map<String, JerryServletContext> contexts){
        Map<String, HttpRequestHandler> handlers = new HashMap<>();
        for (String contextName : contexts.keySet()){
            JerryServletContext context = contexts.get(contextName);
            Set<String> filterNames = context.getFilterRegistrations().keySet();
            for (String filterName : filterNames){
                context.getFilterRegistration(filterName);
            }
            new HttpRequestHandler() {
                @Override
                public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

                }
            };
        }
        return handlers;
    }

    @Bean
    public HttpServer httpServer(Map<String, HttpRequestHandler> handlers){
        HttpProcessor httpProcessor = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("${responseHeader}"))
                .add(new ResponseContent())
                .add(new ResponseConnControl())
                .build();

        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Integer.valueOf("${timeout}"))
                .setTcpNoDelay(true)
                .build();

        ServerBootstrap bootstrap = ServerBootstrap.bootstrap()
                .setHttpProcessor(httpProcessor)
                .setSocketConfig(socketConfig)
                .setListenerPort(Integer.valueOf("${port}"));

        for (String pattern : handlers.keySet()){
            bootstrap.registerHandler(pattern, handlers.get(pattern));
        }

        return bootstrap.create();
    }

}
