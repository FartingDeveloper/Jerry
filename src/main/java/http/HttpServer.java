package http;

import org.springframework.beans.factory.annotation.Autowired;
import servlet.JerryHttpSession;
import servlet.context.JerryServletContext;
import servlet.request.JerryHttpServletRequest;
import servlet.response.JerryHttpServletResponse;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class HttpServer extends Thread{

    @Autowired
    private ServerSocket serverSocket;

    @Autowired
    private ExecutorService threadPool;

    @Autowired
    private Map<String, JerryServletContext> contexts;

    private Map<String, RequestHandler> urls;

    public void init(){
        try {
            urls = createHandlers();
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
                        int index = uri.indexOf("/");

                        RequestHandler handler = urls.get(uri.substring(index + 1, uri.length()));
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

            Set<ServletRequestListener> listeners = servletContext.getRequestListeners();

            Set<String> filterNames = servletContext.getFilterRegistrations().keySet();
            for (final String filterName : filterNames){
                FilterRegistration filterRegistration = servletContext.getFilterRegistration(filterName);
                for (final String url : filterRegistration.getUrlPatternMappings()){
                    RequestHandler handler = createHandler(servletContext, listeners, url);
                    handlers.put(url, handler);
                }
            }

            Set<String> servletNames = servletContext.getServletRegistrations().keySet();
            for (final String servletName : servletNames){
                ServletRegistration servletRegistration = servletContext.getServletRegistration(servletName);
                for (final String url : servletRegistration.getMappings()){
                    RequestHandler handler = createHandler(servletContext, listeners, url);
                    handlers.put(url, handler);
                }
            }

            servletContext.init();

        }
        return handlers;
    }

    private RequestHandler createHandler(JerryServletContext servletContext, Set<ServletRequestListener> listeners, String url){
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

            session.setLastAccesedTime(LocalTime.now().toNanoOfDay());

            JerryHttpServletResponse servletResponse = new JerryHttpServletResponse(response, servletContext);
            JerryHttpServletRequest servletRequest = new JerryHttpServletRequest(request, servletResponse, servletContext);

            servletRequest.setSession(session);

            for (ServletRequestListener listener : listeners){
                listener.requestInitialized(new ServletRequestEvent(servletContext, servletRequest));
            }

            try {
                servletContext.getRequestDispatcher(url).forward(servletRequest, servletResponse);
                servletResponse.flushBuffer();
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (ServletRequestListener listener : listeners){
                listener.requestDestroyed(new ServletRequestEvent(servletContext, servletRequest));
            }
        };
    }
}
