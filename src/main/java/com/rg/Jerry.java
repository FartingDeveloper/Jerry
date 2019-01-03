package com.rg;

import com.rg.http.HttpRequestHandler;
import com.rg.http.HttpServer;
import com.rg.http.RequestHandler;
import com.rg.http.core.HTTP;
import com.rg.loader.ContextLoader;
import com.rg.loader.xml.XmlContextLoader;
import com.rg.servlet.context.JerryServletContext;
import com.rg.servlet.registration.JerryFilterRegistration;
import com.rg.servlet.registration.JerryServletRegistration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;

public class Jerry {

    private static final Logger LOG = LogManager.getLogger(Jerry.class);

    private HttpServer server;

    public Jerry(Properties properties) throws IOException {
        Map<String, JerryServletContext> contexts = loadServletContexts();
        Map<String, RequestHandler> requestHandlers = createRequestHandlers(contexts);

        ServerSocket serverSocket = createServerSocket(properties);

        ExecutorService threadPool = createThreadPool(properties);

        this.server = new HttpServer(serverSocket, threadPool, requestHandlers);
    }

    public void start() {
        server.run();
    }

    private static String getRootPath() {
        File jar = null;
        try {
            jar = new File(Jerry.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            LOG.debug("Root path: " + jar.getAbsolutePath());
        } catch (URISyntaxException e) {
            LOG.error("Root path can't be found.");
            throw new RuntimeException("Root path can't be found.");
        }
        return jar.getParent();
    }

    private Map<String, JerryServletContext> loadServletContexts() {
        String rootPath = getRootPath();
        ContextLoader contextLoader = new XmlContextLoader();
        return contextLoader.load(rootPath);
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

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(Jerry.class.getResourceAsStream("/server.properties"));

        Jerry jerry = new Jerry(properties);
        jerry.start();
    }

}
