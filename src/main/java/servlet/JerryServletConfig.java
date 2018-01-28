package servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

public class JerryServletConfig implements ServletConfig{

    private ServletContext context;
    private String servletName;
    private Map<String, String> initParams;

    public JerryServletConfig(ServletContext context, ServletRegistration servletRegistration){
        this.context = context;
        this.servletName = servletRegistration.getName();
        this.initParams = servletRegistration.getInitParameters();
    }

    public JerryServletConfig(ServletContext context, String servletName, Map<String, String> initParams){
        this.context = context;
        this.servletName = servletName;
        this.initParams = initParams;
    }

    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public String getInitParameter(String name) {
        return initParams.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return new Enumeration<String>() {

            Iterator<String> keys = initParams.keySet().iterator();

            @Override
            public boolean hasMoreElements() {
                return keys.hasNext();
            }

            @Override
            public String nextElement() {
                return keys.next();
            }
        };
    }
}
