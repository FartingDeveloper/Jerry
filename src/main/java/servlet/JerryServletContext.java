package servlet;


import org.apache.logging.log4j.LogManager;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class JerryServletContext implements ServletContext {

    private String contextPath;
    private Map<String, JerryServletRegistration> servletRegistrations;
    private Map<String, JerryFilterRegistration> filterRegistrations;
    private Set<String> resourcePaths;
    private Map<String, String> initParameters;
    private Map<String, ServletContext> contexts;
    private ClassLoader classLoader;

    private Map<String, Object> attributes = new HashMap<>();

    private org.apache.logging.log4j.Logger logger = LogManager.getLogger("servlet.JerryServletContext");

    public JerryServletContext(Map<String, String> contextParameters,
                               Map<String, JerryServletRegistration> servletRegistrations,
                               Map<String, JerryFilterRegistration> filterRegistrations,
                               Set<String> resourcePaths, Map<String, String> initParameters, Map<String, ServletContext> contexts, ClassLoader classLoader){
        this.contextPath = "";
        this.servletRegistrations = servletRegistrations;
        this.filterRegistrations = filterRegistrations;
        this.resourcePaths = resourcePaths;
        this.initParameters = initParameters;
        this.contexts = contexts;
        this.classLoader = classLoader;
    }

    public JerryServletContext(String contextPath, Map<String, String> contextParameters,
                               Map<String, JerryServletRegistration> servletRegistrations,
                               Map<String, JerryFilterRegistration> filterRegistrations,
                               Set<String> resourcePaths, Map<String, String> initParameters, Map<String, ServletContext> contexts, ClassLoader classLoader){
        this.contextPath = contextPath;
        this.servletRegistrations = servletRegistrations;
        this.filterRegistrations = filterRegistrations;
        this.resourcePaths = resourcePaths;
        this.initParameters = initParameters;
        this.contexts = contexts;
        this.classLoader = classLoader;
    }

    public String getContextPath() {
        return contextPath;
    }

    public ServletContext getContext(String uripath) {
        return contexts.get(uripath);
    }

    public int getMajorVersion() {
        return 4;
    }

    public int getMinorVersion() {
        return 0;
    }

    public int getEffectiveMajorVersion() {
        return 4;
    }

    public int getEffectiveMinorVersion() {
        return 0;
    }

    public String getMimeType(String file) {
        String mimetype = null;
        for(String resource : resourcePaths){
            if(comparePaths(file, resource)){
                mimetype = new MimetypesFileTypeMap().getContentType(resource);
            }
        }
        return mimetype;
    }

    public Set<String> getResourcePaths(String path) {
        Set<String> paths = new HashSet<>();

        for(String resource : resourcePaths){
            if(comparePaths(path, resource)){
                paths.add(resource);
            }
        }

        if (path.isEmpty()){
            return null;
        }
        return paths;
    }

    public URL getResource(String path) throws MalformedURLException {
        URL url = null;
        for(String resource : resourcePaths){
            if (comparePaths(path, resource)){
                url = new URL(resource);
            }
        }
        return url;
    }

    private boolean comparePaths(String path, String resource){
        Pattern pattern = Pattern.compile(path + "(\\w*\\" + File.pathSeparator + "| \\w*$)");
        return pattern.matcher(resource).find();
    }

    public InputStream getResourceAsStream(String path) {
        InputStream in = null;
        try {
            File file = new File(getResource(path).getPath());
            in = new FileInputStream(file);
        } catch (MalformedURLException e) {
            logger.error("Wrong resource name.");
        } catch (FileNotFoundException e) {
            logger.error("File isn't found.");
        }
        return in;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        for (JerryServletRegistration servletRegistration : servletRegistrations.values()){
            for (String p : servletRegistration.getMappings()){
                if(comparePaths(path, p)){
                    return servletRegistration.getJerryRequestDispatcher(this);
                }
            }
        }
        return null;
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        return servletRegistrations.get(name).getJerryRequestDispatcher(this);
    }

    public Servlet getServlet(String name) throws ServletException {
        return null;
    }

    public Enumeration<Servlet> getServlets() {
        return new Enumeration<Servlet>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }

            @Override
            public Servlet nextElement() {
                return null;
            }
        };
    }

    public Enumeration<String> getServletNames() {
        return new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }

            @Override
            public String nextElement() {
                return null;
            }
        };
    }

    public void log(String msg) {
        logger.info(msg);
    }

    public void log(Exception exception, String msg) {
        log(msg, exception);
    }

    public void log(String message, Throwable throwable) {
        logger.error(message + throwable.getStackTrace());
    }

    public String getRealPath(String path) {
        return null;
    }

    public String getServerInfo() {
        return "Jerry";
    }

    public String getInitParameter(String name) {
        return initParameters.get(name);
    }

    public Enumeration<String> getInitParameterNames() {
        return new Enumeration<String>() {

            Iterator<String> parameterNames = initParameters.keySet().iterator();

            @Override
            public boolean hasMoreElements() {
                return parameterNames.hasNext();
            }

            @Override
            public String nextElement() {
                return parameterNames.next();
            }
        };
    }

    public boolean setInitParameter(String name, String value) {
        boolean result = initParameters.containsKey(name);
        initParameters.put(name, value);
        return result;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        return new Enumeration<String>() {

            Iterator<String> attributeNames = attributes.keySet().iterator();

            @Override
            public boolean hasMoreElements() {
                return attributeNames.hasNext();
            }

            @Override
            public String nextElement() {
                return attributeNames.next();
            }
        };
    }

    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public String getServletContextName() {
        return null;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return null;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return null;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return null;
    }

    public ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) {
        return null;
    }

    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        return null;
    }

    public ServletRegistration getServletRegistration(String servletName) {
        return servletRegistrations.get(servletName);
    }

    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return null;
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return null;
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return null;
    }

    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        return null;
    }

    public FilterRegistration getFilterRegistration(String filterName) {
        return null;
    }

    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return null;
    }

    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {

    }

    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return null;
    }

    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return null;
    }

    public void addListener(String className) {

    }

    public <T extends EventListener> void addListener(T t) {

    }

    public void addListener(Class<? extends EventListener> listenerClass) {

    }

    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        return null;
    }

    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void declareRoles(String... roleNames) {

    }

    public String getVirtualServerName() {
        return null;
    }

    public int getSessionTimeout() {
        return 0;
    }

    public void setSessionTimeout(int sessionTimeout) {

    }

    public String getRequestCharacterEncoding() {
        return null;
    }

    public void setRequestCharacterEncoding(String encoding) {

    }

    public String getResponseCharacterEncoding() {
        return null;
    }

    public void setResponseCharacterEncoding(String encoding) {

    }
}
