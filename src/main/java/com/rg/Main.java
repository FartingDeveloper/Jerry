package com.rg;

import com.rg.config.ServerConfig;
import com.rg.http.HttpServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ServerConfig.class);
        applicationContext.refresh();

        HttpServer server = applicationContext.getBean(HttpServer.class);
        server.start();
    }
}
