package com.rg.http;

import com.rg.http.io.HttpRequest;
import com.rg.http.io.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class HttpServer extends Thread {

    private static final Logger LOG = LogManager.getLogger(HttpServer.class);

    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private Map<String, RequestHandler> services;

    public HttpServer(ServerSocket serverSocket, ExecutorService executorService, Map<String, RequestHandler> services) throws IOException {
        this.serverSocket = serverSocket;
        this.threadPool = executorService;
        this.services = services;
    }

    @Override
    public void run() {
        while (true) {
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
