package com.rg.servlet.registration;

import org.apache.logging.log4j.LogManager;
import com.rg.servlet.JerryEnumeration;
import com.rg.servlet.context.JerryServletContext;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import java.io.IOException;
import java.util.*;

public class JerryServletRegistration extends JerryRegistration implements ServletRegistration.Dynamic {

    private Servlet servlet;
    private String role;
    private int loadOnStartup;

    private Set<String> mappings;
    protected JerryServletRegistration.JerryServletConfig config;

    protected Logger LOG = LogManager.getLogger(JerryServletRegistration.class);

    public JerryServletRegistration(String servletName, String servletClassName){
        super(servletName, servletClassName);
        mappings = new LinkedHashSet<>();
        loadOnStartup = -1;
    }

    @Override
    public Set<String> addMapping(String... urlPatterns) {
        check(urlPatterns);

        for (String url : urlPatterns){
            mappings.add(url);
        }
        return mappings;
    }

    @Override
    public Collection<String> getMappings() {
        return mappings;
    }

    @Override
    public String getRunAsRole() {
        return role;
    }

    public JerryServletConfig getJerryServletConfig(ServletContext context){
        if(config == null){
            config = new JerryServletConfig(context);
        }
        return config;
    }

    @Override
    public void setLoadOnStartup(int loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }

    public int getLoadOnStartup() {
        return loadOnStartup;
    }

    @Override
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        return null;
    }

    @Override
    public void setMultipartConfig(MultipartConfigElement multipartConfig) {

    }

    @Override
    public void setRunAsRole(String roleName) {
        this.role = roleName;
    }

    public Servlet getServlet() {
        return servlet;
    }

    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    public void init(ServletContext context) throws InstantiationException, ClassNotFoundException, ServletException, IllegalAccessException {
        if(initialized){
            throw new IllegalStateException();
        }

        initialized = true;

        if(servlet == null){
            Class<Servlet> clazz = (Class<Servlet>) context.getClassLoader().loadClass(className);
            servlet = clazz.newInstance();
        }

        servlet.init(new JerryServletConfig(context));
    }


    private class JerryServletConfig implements ServletConfig{

        private ServletContext context;

        public JerryServletConfig(ServletContext context){
            this.context = context;
        }

        @Override
        public String getServletName() {
            return name;
        }

        @Override
        public ServletContext getServletContext() {
            return context;
        }

        @Override
        public String getInitParameter(String name) {
            return initParameters.get(name);
        }

        @Override
        public Enumeration<String> getInitParameterNames() {
            return new JerryEnumeration<>(initParameters.keySet().iterator());
        }
    }
}
