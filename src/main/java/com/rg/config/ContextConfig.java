package com.rg.config;

import com.rg.http.core.HTTP;
import com.rg.http.HttpRequestHandler;
import com.rg.http.RequestHandler;
import com.rg.loader.XmlContextContextLoader;
import com.rg.loader.ContextLoader;
import com.rg.servlet.registration.JerryFilterRegistration;
import com.rg.servlet.registration.JerryServletRegistration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.rg.servlet.context.JerryServletContext;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import java.io.File;
import java.net.URISyntaxException;
import java.util.*;

@Configuration
public class ContextConfig {

    private Logger LOG = LogManager.getLogger(ContextConfig.class);

    @Resource(name = "contexts")
    private Map<String, JerryServletContext> contexts;

    @Bean
    public String getRootPath() {
        File jar = null;
        try {
            jar = new File(ContextConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            LOG.debug("Root path: " + jar.getAbsolutePath());
        } catch (URISyntaxException e) {
            LOG.error("Root path can't be found.");
            throw new RuntimeException("Root path can't be found.");
        }
        return jar.getParent();
    }

    @Bean
    public ContextLoader getContextLoader() {
        return new XmlContextContextLoader();
    }

    @Bean("contexts")
    public Map<String, JerryServletContext> getServletContexts(ContextLoader contextLoader, String rootPath) {
        return contextLoader.load(rootPath);
    }

    @Bean("services")
    public Map<String, RequestHandler> getRequestHandlers() {
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
