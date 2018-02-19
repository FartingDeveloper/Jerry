package config;

import http.HttpServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.net.ServerSocket;

@Configuration
@Import({ContextConfig.class, ThreadPoolConfig.class,})
@PropertySource("classpath:/com/rg/server.properties")
public class ServerConfig {

    @Value("${port}")
    private int port;

    @Bean(destroyMethod = "close")
    public ServerSocket serverSocket() throws IOException {
        return new ServerSocket(port);
    }

//    @Bean
//    public Map<String, RequestHandler> handlers(Map<String, JerryServletContext> contexts){
//        Map<String, RequestHandler> handlers = new LinkedHashMap<>();
//
//        for (String contextName : contexts.keySet()){
//
//            JerryServletContext servletContext = contexts.get(contextName);
//
//            Set<ServletRequestListener> listeners = servletContext.getRequestListeners();
//
//            Set<String> filterNames = servletContext.getFilterRegistrations().keySet();
//            for (final String filterName : filterNames){
//                FilterRegistration filterRegistration = servletContext.getFilterRegistration(filterName);
//                for (final String url : filterRegistration.getUrlPatternMappings()){
//                    RequestHandler handler = createHandler(servletContext, listeners, url);
//                    handlers.put(url, handler);
//                }
//            }
//
//            Set<String> servletNames = servletContext.getServletRegistrations().keySet();
//            for (final String servletName : servletNames){
//                ServletRegistration servletRegistration = servletContext.getServletRegistration(servletName);
//                for (final String url : servletRegistration.getMappings()){
//                    RequestHandler handler = createHandler(servletContext, listeners, url);
//                    handlers.put(url, handler);
//                }
//            }
//        }
//        return handlers;
//    }
//
//    private RequestHandler createHandler(JerryServletContext servletContext, Set<ServletRequestListener> listeners, String url){
//        return (HttpRequest request, HttpResponse response)->{
//            Thread.currentThread().setContextClassLoader(servletContext.getClassLoader());
//
//            String sessionId = null;
//            for(HeaderElement element : request.getFirstHeader("Cookie").getElements()){
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
//        };
//    }

    @Bean
    public HttpServer server(){
        return new HttpServer();
    }

//    @Bean
//    public HttpServer httpServer(Map<String, HttpRequestHandler> createHandlers){
//        HttpProcessor httpProcessor = HttpProcessorBuilder.create()
//                .add(new ResponseDate())
//                .add(new ResponseServer("${responseHeader}"))
//                .add(new ResponseContent())
//                .add(new ResponseConnControl())
//                .build();
//
//        SocketConfig socketConfig = SocketConfig.custom()
//                .setSoTimeout(Integer.valueOf("${timeout}"))
//                .setTcpNoDelay(true)
//                .build();
//
//        ServerBootstrap bootstrap = ServerBootstrap.bootstrap()
//                .setHttpProcessor(httpProcessor)
//                .setSocketConfig(socketConfig)
//                .setListenerPort(Integer.valueOf("${port}"));
//
//        for (String pattern : createHandlers.keySet()){
//            bootstrap.registerHandler(pattern, createHandlers.get(pattern));
//        }
//
//        return bootstrap.create();
//    }

}
