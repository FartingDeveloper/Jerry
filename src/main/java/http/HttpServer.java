package http;

import org.springframework.beans.factory.annotation.Autowired;
import servlet.JerryHttpSession;
import servlet.context.JerryServletContext;
import servlet.registration.JerryFilterChain;
import servlet.registration.JerryFilterRegistration;
import servlet.registration.JerryServletRegistration;
import servlet.request.JerryHttpServletRequest;
import servlet.response.JerryHttpServletResponse;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
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
    private ExecutorService threadPool;

    @Autowired
    private Map<String, JerryServletContext> contexts;

    private Map<String, RequestHandler> services;

    public void init(){
        try {
            services = createHandlers();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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

                            if(response.getStatusCode() == -1){
                                response.setStatusCode(HttpServletResponse.SC_FOUND);
                            }

                        } else{
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND, "NOT FOUND");
                        }

                        response.flush();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (HttpParser.WrongRequestException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace(); //LOG4G
            }
        }
    }

    private Map<String, RequestHandler> createHandlers() throws ClassNotFoundException, InstantiationException, ServletException, IllegalAccessException {
        Map<String, RequestHandler> handlers = new LinkedHashMap<>();

        for (String contextName : contexts.keySet()){

            JerryServletContext servletContext = contexts.get(contextName);

            Set<String> servletNames = servletContext.getServletRegistrations().keySet();
            for (final String servletName : servletNames){
                JerryServletRegistration servletRegistration = (JerryServletRegistration) servletContext.getServletRegistration(servletName);
                for (final String url : servletRegistration.getMappings()){

                    List<Filter> filters = new LinkedList<>();
                    Map<String, JerryFilterRegistration> filterRegistrations = (Map<String, JerryFilterRegistration>) servletContext.getFilterRegistrations();
                    for(JerryFilterRegistration filterRegistration : filterRegistrations.values()){
                        for (String mapping : filterRegistration.getUrlPatternMappings()){
                            if(url.contains(mapping)){
                                filters.add(filterRegistration.getFilter());
                            }
                        }
                    }

                    RequestHandler handler = createHandler(servletContext, servletRegistration.getServlet(), filters);
                    handlers.put(url, handler);
                }
            }

            servletContext.init();

        }
        return handlers;
    }

    private RequestHandler createHandler(JerryServletContext servletContext, Servlet servlet, List<Filter> filters){
        return (HttpRequest request, HttpResponse response)->{
            Thread.currentThread().setContextClassLoader(servletContext.getClassLoader());

            String sessionId = null;
            for(HeaderElement element : request.getHeader("Cookie").getElements()){
                if(element.getName().equals("JSESSIONID")){
                    sessionId = element.getValue();
                }
            }

            JerryHttpSession session = null;
            if(sessionId == null){
                session = servletContext.createSession(response);
            } else{
                session = servletContext.getSession(sessionId);
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
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (ServletRequestListener listener : servletContext.getRequestListeners()){
                listener.requestDestroyed(new ServletRequestEvent(servletContext, servletRequest));
            }
        };
    }
}
