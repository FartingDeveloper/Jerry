package com.rg.http;

import com.rg.http.io.HttpRequest;
import com.rg.http.io.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;
import java.util.concurrent.ExecutorService;

@Component
public class HttpServer extends Thread {

    @Autowired
    private ServerSocket serverSocket;
    @Autowired
    @Qualifier("threadPool")
    private ExecutorService threadPool;
    @Resource(name = "services")
    private Map<String, RequestHandler> services;

    private Logger LOG = LogManager.getLogger(HttpServer.class);

    @Override
    public void run() {
        for (; ; ) {
            try {
                Socket socket = serverSocket.accept();
                threadPool.execute(() -> {
                    try {
                        HttpRequest request = HttpParser.parse(socket.getInputStream());
                        HttpResponse response = new HttpResponse(socket.getOutputStream(), request.getRequestLine());

                        String uri = request.getRequestLine().getUri();

                        RequestHandler handler = services.get(uri);
                        if (handler != null) {
                            handler.handle(request, response);
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND, "NOT FOUND");
                        }

                        response.flush();
                        socket.close();
                    } catch (IOException e) {
                        LOG.error("Can't create response!", e);
                    } catch (HttpParser.WrongRequestException e) {
                        LOG.error("Can't parse http request!", e);
                    }
                });
            } catch (Exception e) {
                LOG.error("Can't create response!", e);
            }
        }
    }
}
