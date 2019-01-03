package com.rg.http;

import com.rg.http.core.HTTP;
import com.rg.http.io.HttpRequest;
import com.rg.http.io.HttpResponse;
import com.rg.servlet.context.JerryServletContext;
import com.rg.servlet.registration.JerryFilterRegistration;
import com.rg.servlet.registration.JerryServletRegistration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;
import java.util.concurrent.*;

public class HttpServer extends Thread {

    private static final Logger LOG = LogManager.getLogger(HttpServer.class);

    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private Map<String, RequestHandler> services;

    public HttpServer(Properties properties, Map<String, JerryServletContext> contexts) throws IOException {
        this.serverSocket = createServerSocket(properties);
        this.threadPool = createThreadPool(properties);
        this.services = createRequestHandlers(contexts);
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

    private ServerSocket createServerSocket(Properties properties) throws IOException {
        int port = Integer.parseInt(properties.getProperty("port"));
        return new ServerSocket(port);
    }

    private ExecutorService createThreadPool(Properties properties) {
        int capacity = Integer.parseInt(properties.getProperty("threadPool.capacity"));
        int corePoolSize = Integer.parseInt(properties.getProperty("threadPool.corePoolSize"));
        int maximumPoolSize = Integer.parseInt(properties.getProperty("threadPool.maximumPoolSize"));
        long keepAliveTime = Integer.parseInt(properties.getProperty("queue.keepAliveTime"));

        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(capacity);

        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MINUTES, blockingQueue);
    }

    private Map<String, RequestHandler> createRequestHandlers(Map<String, JerryServletContext> contexts) {
        Map<String, RequestHandler> handlers = new LinkedHashMap<>();

        for (String contextName : contexts.keySet()) {

            JerryServletContext servletContext = contexts.get(contextName);

            try {
                servletContext.init();
            } catch (ClassNotFoundException e) {
                LOG.error("Class is not found error: " + contextName, e);
                throw new IllegalStateException(e.getCause());
            } catch (InstantiationException e) {
                LOG.error("Context creation error: " + contextName, e);
                throw new IllegalStateException(e.getCause());
            } catch (ServletException e) {
                LOG.error("Context creation error: " + contextName, e);
                throw new IllegalStateException(e.getCause());
            } catch (IllegalAccessException e) {
                LOG.error("Context creation error: " + contextName, e);
                throw new IllegalStateException(e.getCause());
            }

            Set<String> servletNames = servletContext.getServletRegistrations().keySet();
            for (final String servletName : servletNames) {
                JerryServletRegistration servletRegistration = (JerryServletRegistration) servletContext.getServletRegistration(servletName);
                for (final String url : servletRegistration.getMappings()) {

                    List<Filter> filters = new LinkedList<>();
                    Map<String, JerryFilterRegistration> filterRegistrations = (Map<String, JerryFilterRegistration>) servletContext.getFilterRegistrations();
                    for (JerryFilterRegistration filterRegistration : filterRegistrations.values()) {
                        for (String mapping : filterRegistration.getUrlPatternMappings()) {
                            int index = mapping.lastIndexOf("*");
                            if (index != -1) {
                                mapping = mapping.substring(0, index);
                            }

                            if (url.contains(mapping)) {
                                filters.add(filterRegistration.getFilter());
                            }
                        }
                    }

                    RequestHandler handler = new HttpRequestHandler(servletContext, servletRegistration.getServlet(), filters);
                    handlers.put(HTTP.URL_SEPARATOR + contextName + url, handler);
                }
            }
        }
        return handlers;
    }
}
