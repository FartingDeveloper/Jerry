package servlet;

import org.apache.logging.log4j.LogManager;

import javax.servlet.*;
import java.io.IOException;
import java.util.*;

public class JerryServletRegistration extends JerryRegistration implements ServletRegistration {

    protected String role;
    protected int loadOnStartup;

    private Set<String> mappings;
    protected JerryServletRegistration.JerryServletConfig config;

    protected org.apache.logging.log4j.Logger logger = LogManager.getLogger("servlet.JerryServletRegistration");

    public JerryServletRegistration(String servletName, String servletClassName){
        super(servletName, servletClassName);
        mappings = new LinkedHashSet<>();
        loadOnStartup = -1;
    }

    public JerryServletRegistration(String servletName, String servletClassName, Map<String, String> initParameters){
        super(servletName, servletClassName, initParameters);
        mappings = new LinkedHashSet<>();
        loadOnStartup = -1;
    }

    public JerryServletRegistration(String servletName, String servletClassName, Map<String, String> initParameters, Set<String> mappings){
        this(servletName, servletClassName, initParameters);
        this.mappings = mappings;
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

    public JerryServletConfig.JerryRequestDispatcher getJerryRequestDispatcher(ServletContext context){
        JerryServletConfig config = getJerryServletConfig(context);
        JerryServletConfig.JerryRequestDispatcher requestDispatcher = null;
        try {
            requestDispatcher = config.getJerryRequestDispatcher();
        } catch (ClassNotFoundException e) {
            logger.error("Class isn't found: " + className);
            throw new RuntimeException("Class isn't found");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return requestDispatcher;
    }

    protected class JerryServletConfig implements ServletConfig{

        private JerryRequestDispatcher requestDispatcher;

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

        public JerryRequestDispatcher getJerryRequestDispatcher() throws ClassNotFoundException, InstantiationException, ServletException, IllegalAccessException {
            if(requestDispatcher == null){
                requestDispatcher = new JerryRequestDispatcher();
            }
            return requestDispatcher;
        }

        protected class JerryRequestDispatcher implements RequestDispatcher {

            private Servlet servlet;

            public JerryRequestDispatcher() throws ClassNotFoundException, IllegalAccessException, InstantiationException, ServletException {
                ClassLoader classLoader = context.getClassLoader();
                Class<Servlet> servletClass = (Class<Servlet>) classLoader.loadClass(className);
                servlet = servletClass.newInstance();
                servlet.init(JerryServletConfig.this);
            }

            public JerryRequestDispatcher(Servlet servlet){
                this.servlet = servlet;
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
