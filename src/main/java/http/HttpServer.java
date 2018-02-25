package http;

import org.springframework.beans.factory.annotation.Autowired;
import servlet.JerryHttpSession;
import servlet.context.JerryServletContext;
import servlet.request.JerryHttpServletRequest;
import servlet.response.JerryHttpServletResponse;

import javax.servlet.*;
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
        urls = createHandlers();
    }

    @Override
    public void run() {
        for(;;){
            try {
                Socket socket = serverSocket.accept();
                threadPool.execute(()->{
                    try{
                        HttpRequest request = HttpParser.parse(socket);
                        HttpResponse response = new HttpResponse(socket.getOutputStream());

                        urls.get("url").handle(request, response);
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

    private Map<String, RequestHandler> createHandlers(){
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
        }
        return handlers;
    }

    private RequestHandler createHandler(JerryServletContext servletContext, Set<ServletRequestListener> listeners, String url){
        return (HttpRequest request, HttpResponse response)->{
//            Thread.currentThread().setContextClassLoader(servletContext.getClassLoader());
//
//            String sessionId = null;
//            for(HeaderElement element : request.getHeader("Cookie").getElements()){
//                if(element.getName().equals("JSESSIONID")){
//                    sessionId = element.getValue();
//                }
//            }
//
//            JerryHttpSession session = null;
//            if(sessionId == null){
//                session = servletContext.createSession(response);
//            } else{
//                session = servletContext.getSession(sessionId);
//            }
//
//            session.setLastAccesedTime(LocalTime.now().toNanoOfDay());
//
//            JerryHttpServletResponse servletResponse = new JerryHttpServletResponse(response, servletContext);
//            JerryHttpServletRequest servletRequest = new JerryHttpServletRequest(request, servletResponse, servletContext);
//
//            servletRequest.setSession(session);
//
//            for (ServletRequestListener listener : listeners){
//                listener.requestInitialized(new ServletRequestEvent(servletContext, servletRequest));
//            }
//
//            try {
//                servletContext.getRequestDispatcher(url).forward(servletRequest, servletResponse);
//                servletResponse.flushBuffer();
//            } catch (ServletException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            for (ServletRequestListener listener : listeners){
//                listener.requestDestroyed(new ServletRequestEvent(servletContext, servletRequest));
//            }
        };
    }
}
