package servlet;

import org.apache.logging.log4j.LogManager;

import javax.servlet.*;
import java.io.IOException;
import java.util.*;

public class JerryServletRegistration implements ServletRegistration {

    private String servletName;
    private String servletClassName;
    private Map<String, String> initParameters;
    private Set<String> mappings;

    private Map<ServletContext, JerryServletConfig> cache;

    private org.apache.logging.log4j.Logger logger = LogManager.getLogger("servlet.JerryServletRegistration");

    public JerryServletRegistration(String servletName, String servletClassName){
        this.servletName = servletName;
        this.servletClassName = servletClassName;
        initParameters = new HashMap<>();
        mappings = new HashSet<>();
        cache = new HashMap<>();
    }

    public JerryServletRegistration(String servletName, String servletClassName, Map<String, String> initParameters){
        this(servletName, servletClassName);
        this.initParameters = initParameters;
    }

    public JerryServletRegistration(String servletName, String servletClassName, Set<String> mappings){
        this(servletName, servletClassName);
        this.mappings = mappings;
    }

    public JerryServletRegistration(String servletName, String servletClassName, Map<String, String> initParameters, Set<String> mappings){
        this(servletName, servletClassName);
        this.initParameters = initParameters;
        this.mappings = mappings;
    }

    @Override
    public Set<String> addMapping(String... urlPatterns) {
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
        return null;
    }

    @Override
    public String getName() {
        return servletName;
    }

    @Override
    public String getClassName() {
        return servletClassName;
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        boolean result = initParameters.containsKey(name);
        initParameters.put(name, value);
        return result;
    }

    @Override
    public String getInitParameter(String name) {
        return initParameters.get(name);
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        initParameters.putAll(initParameters);
        return initParameters.keySet();
    }

    @Override
    public Map<String, String> getInitParameters() {
        return initParameters;
    }

    public JerryServletConfig getJerryServletConfig(ServletContext context){
        if(cache.get(context) == null){
            cache.put(context, new JerryServletConfig(context));
        }
        return cache.get(context);
    }

    public JerryServletConfig.JerryRequestDispatcher getJerryRequestDispatcher(ServletContext context){
        JerryServletConfig config = getJerryServletConfig(context);
        JerryServletConfig.JerryRequestDispatcher requestDispatcher = null;
        try {
            requestDispatcher = config.getJerryRequestDispatcher();
        } catch (ClassNotFoundException e) {
            logger.error("Class isn't found: " + servletClassName);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return requestDispatcher;
    }

    private class JerryServletConfig implements ServletConfig{

        private JerryRequestDispatcher requestDispatcher;

        private ServletContext context;

        public JerryServletConfig(ServletContext context){
            this.context = context;
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
            return initParameters.get(name);
        }

        @Override
        public Enumeration<String> getInitParameterNames() {
            return new Enumeration<String>() {

                Iterator<String> keys = initParameters.keySet().iterator();

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

        public JerryRequestDispatcher getJerryRequestDispatcher() throws ClassNotFoundException, InstantiationException, ServletException, IllegalAccessException {
            if(requestDispatcher == null){
                requestDispatcher = new JerryRequestDispatcher();
            }
            return requestDispatcher;
        }

        private class JerryRequestDispatcher implements RequestDispatcher {

            private Servlet servlet;

            private JerryRequestDispatcher() throws ClassNotFoundException, IllegalAccessException, InstantiationException, ServletException {
                ClassLoader classLoader = context.getClassLoader();
                Class<Servlet> servletClass = (Class<Servlet>) classLoader.loadClass(servletClassName);
                servlet = servletClass.newInstance();
                servlet.init(JerryServletConfig.this);
            }

            @Override
            public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
                if(response.isCommitted()){
                    throw new IllegalStateException();
                }
                servlet.service(request, response);
            }

            @Override
            public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
                servlet.service(request, response);
            }

            public Servlet getServlet() {
                return servlet;
            }
        }
    }
}
