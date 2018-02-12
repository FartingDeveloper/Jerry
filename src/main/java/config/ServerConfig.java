package config;

import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import servlet.request.JerryServletRequest;
import servlet.response.JerryServletResponse;
import servlet.context.JerryServletContext;

import javax.servlet.*;
import javax.servlet.http.HttpSession;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Configuration
@Import(ContextConfig.class)
@PropertySource("classpath:/com/rg/server.properties")
public class ServerConfig {

    @Value("${port}")
    private int port;

//    @Bean(destroyMethod = "close")
//    public ServerSocket serverSocket() throws IOException {
//        return new ServerSocket(port);
//    }

    @Bean
    public Map<String, HttpRequestHandler> handlers(Map<String, JerryServletContext> contexts){
        Map<String, HttpRequestHandler> handlers = new LinkedHashMap<>();

        for (String contextName : contexts.keySet()){

            JerryServletContext servletContext = contexts.get(contextName);

            Set<ServletRequestListener> listeners = servletContext.getRequestListeners();

            Set<String> filterNames = servletContext.getFilterRegistrations().keySet();
            for (final String filterName : filterNames){
                FilterRegistration filterRegistration = servletContext.getFilterRegistration(filterName);
                for (final String url : filterRegistration.getUrlPatternMappings()){
                    HttpRequestHandler handler = createHandler(servletContext, listeners, url);
                    handlers.put(url, handler);
                }
            }

            Set<String> servletNames = servletContext.getServletRegistrations().keySet();
            for (final String servletName : servletNames){
                ServletRegistration servletRegistration = servletContext.getServletRegistration(servletName);
                for (final String url : servletRegistration.getMappings()){
                    HttpRequestHandler handler = createHandler(servletContext, listeners, url);
                    handlers.put(url, handler);
                }
            }
        }
        return handlers;
    }

    public HttpRequestHandler createHandler(JerryServletContext servletContext, Set<ServletRequestListener> listeners, String url){
        return (HttpRequest request, HttpResponse response, HttpContext context)->{
            Thread.currentThread().setContextClassLoader(servletContext.getClassLoader());

            String sessionId = null;
            for(HeaderElement element : request.getFirstHeader("Cookie").getElements()){
                if(element.getName().equals("JSESSIONID")){
                    sessionId = element.getValue();
                }
            }

            if(sessionId == null){
                servletContext.createSession(response).setLastAccesedTime(LocalTime.now().toNanoOfDay());
            } else{
                servletContext.getSession(sessionId).setLastAccesedTime(LocalTime.now().toNanoOfDay());
            }

            JerryServletRequest servletRequest = new JerryServletRequest(request, servletContext);
            JerryServletResponse servletResponse = new JerryServletResponse(response, servletContext);

            for (ServletRequestListener listener : listeners){
                listener.requestInitialized(new ServletRequestEvent(servletContext, servletRequest));
            }

            try {
                servletContext.getRequestDispatcher(url).forward(servletRequest, servletResponse);
            } catch (ServletException e) {
                e.printStackTrace();
            }

            for (ServletRequestListener listener : listeners){
                listener.requestDestroyed(new ServletRequestEvent(servletContext, servletRequest));
            }
        };
    }

    @Bean
    public HttpServer httpServer(Map<String, HttpRequestHandler> handlers){
        HttpProcessor httpProcessor = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("${responseHeader}"))
                .add(new ResponseContent())
                .add(new ResponseConnControl())
                .build();

        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Integer.valueOf("${timeout}"))
                .setTcpNoDelay(true)
                .build();

        ServerBootstrap bootstrap = ServerBootstrap.bootstrap()
                .setHttpProcessor(httpProcessor)
                .setSocketConfig(socketConfig)
                .setListenerPort(Integer.valueOf("${port}"));

        for (String pattern : handlers.keySet()){
            bootstrap.registerHandler(pattern, handlers.get(pattern));
        }

        return bootstrap.create();
    }

}
