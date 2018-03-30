package com.rg.http;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.rg.servlet.JerryHttpSession;
import com.rg.servlet.context.JerryServletContext;
import com.rg.servlet.registration.JerryFilterChain;
import com.rg.servlet.registration.JerryFilterRegistration;
import com.rg.servlet.registration.JerryServletRegistration;
import com.rg.servlet.request.JerryHttpServletRequest;
import com.rg.servlet.response.JerryHttpServletResponse;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class HttpServer extends Thread{

    @Autowired
    private ServerSocket serverSocket;

    @Autowired
    @Qualifier("threadPool")
    private ExecutorService threadPool;

    @Autowired
    private Map<String, JerryServletContext> contexts;

    private Map<String, RequestHandler> services;

    private org.apache.logging.log4j.Logger logger = LogManager.getLogger("com.rg.config.HttpServer");

    public void init(){
        services = createHandlers();
    }

    @Override
    public void run() {
        for(;;){
            try {
                Socket socket = serverSocket.accept();
                threadPool.execute(()->{
                    try{
                        HttpRequest request = HttpParser.parse(socket);
                        HttpResponse response = new HttpResponse(socket.getOutputStream(), request.getRequestLine());

                        String uri = request.getRequestLine().getUri();

                        RequestHandler handler = services.get(uri);
                        if(handler != null){
                            handler.handle(request, response);
                        } else{
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND, "NOT FOUND");
                        }

                        response.flush();
                        socket.close();
                    } catch (IOException e) {
                        logger.error("Can't create response", e);
                    } catch (HttpParser.WrongRequestException e) {
                        logger.error("Can't parse com.rg.config.http", e);
                    }
                });
            } catch (IOException e) {
                logger.error("Can't create response", e);
            }
        }
    }

    private Map<String, RequestHandler> createHandlers() {
        Map<String, RequestHandler> handlers = new LinkedHashMap<>();

        for (String contextName : contexts.keySet()){

            JerryServletContext servletContext = contexts.get(contextName);

            try {
                servletContext.init();
            } catch (ClassNotFoundException e) {
                logger.error("Class is not found error: " + contextName, e);
                continue;
            } catch (InstantiationException e) {
                logger.error("Context creation error: " + contextName, e);
                continue;
            } catch (ServletException e) {
                logger.error("Context creation error: " + contextName, e);
                continue;
            } catch (IllegalAccessException e) {
                logger.error("Context creation error: " + contextName, e);
                continue;
            }

            Set<String> servletNames = servletContext.getServletRegistrations().keySet();
            for (final String servletName : servletNames){
                JerryServletRegistration servletRegistration = (JerryServletRegistration) servletContext.getServletRegistration(servletName);
                for (final String url : servletRegistration.getMappings()){

                    List<Filter> filters = new LinkedList<>();
                    Map<String, JerryFilterRegistration> filterRegistrations = (Map<String, JerryFilterRegistration>) servletContext.getFilterRegistrations();
                    for(JerryFilterRegistration filterRegistration : filterRegistrations.values()){
                        for (String mapping : filterRegistration.getUrlPatternMappings()){
                            int index = mapping.lastIndexOf("*");
                            if(index != -1){
                                mapping = mapping.substring(0, index);
                            }

                            if(url.contains(mapping)){
                                filters.add(filterRegistration.getFilter());
                            }
                        }
                    }

                    RequestHandler handler = createHandler(servletContext, servletRegistration.getServlet(), filters);
                    handlers.put(Syntax.URL_SEPARATOR + contextName + url, handler);
                }
            }
        }
        return handlers;
    }

    private RequestHandler createHandler(JerryServletContext servletContext, Servlet servlet, List<Filter> filters){
        return (HttpRequest request, HttpResponse response)->{
            Thread.currentThread().setContextClassLoader(servletContext.getClassLoader());

            String sessionId = null;
            Header cookie = request.getHeader("Cookie");
            if(cookie != null){
                for(HeaderElement element : request.getHeader("Cookie").getElements()){
                    if(element.getName().equals("JSESSIONID")){
                        sessionId = element.getValue();
                    }
                }
            }

            JerryHttpSession session = null;
            if(sessionId == null){
                session = servletContext.createSession(response);
            } else{
                session = servletContext.getSession(sessionId);
            }

            if(session == null){
                session = servletContext.createSession(response);
            }

            session.setLastAccessedTime(LocalTime.now().toNanoOfDay());

            JerryHttpServletResponse servletResponse = new JerryHttpServletResponse(response, servletContext);
            JerryHttpServletRequest servletRequest = new JerryHttpServletRequest(request, servletResponse, servletContext);

            servletRequest.setSession(session);

            for (ServletRequestListener listener : servletContext.getRequestListeners()){
                listener.requestInitialized(new ServletRequestEvent(servletContext, servletRequest));
            }

            try {
                new JerryFilterChain(servlet, filters).doFilter(servletRequest, servletResponse);
                servletResponse.flushBuffer();
            } catch (ServletException e) {
                logger.error("Can't create response", e);
            } catch (IOException e) {
                logger.error("Can't create response", e);
            }

            for (ServletRequestListener listener : servletContext.getRequestListeners()){
                listener.requestDestroyed(new ServletRequestEvent(servletContext, servletRequest));
            }
        };
    }
}
