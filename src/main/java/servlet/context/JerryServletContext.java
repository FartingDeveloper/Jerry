package servlet.context;


import org.apache.logging.log4j.LogManager;
import servlet.JerryEnumeration;
import servlet.registration.JerryFilterRegistration;
import servlet.registration.JerryFilterRegistrationDynamic;
import servlet.registration.JerryServletRegistration;
import servlet.registration.JerryServletRegistrationDynamic;

import javax.activation.MimeType;
import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class JerryServletContext implements ServletContext {

    private boolean initialized;

    private String contextPath;

    private Map<String, String> contextParameters;

    private Map<String, JerryServletRegistration> servletRegistrations;
    private Map<String, JerryFilterRegistration> filterRegistrations;

    private Set<ServletRequestAttributeListener> requestAttributeListeners = new LinkedHashSet<>();
    private Set<HttpSessionAttributeListener> sessionAttributeListeners = new LinkedHashSet<>();
    private Set<HttpSessionBindingListener> sessionBindingListeners = new LinkedHashSet<>();
    private Set<HttpSessionActivationListener> sessionActivationListeners = new LinkedHashSet<>();
    private Set<ServletContextAttributeListener> servletContextAttributeListeners = new LinkedHashSet<>();
    private Set<ServletContextListener> servletContextListeners = new LinkedHashSet<>();
    private Set<HttpSessionListener> sessionListeners = new LinkedHashSet<>();
    private Set<ServletRequestListener> requestListeners = new LinkedHashSet<>();

    private Set<String> resourcePaths;
    private Map<String, MimeType> mimeTypes;

    private int sessionTimeout;

    private String requestEncoding;
    private String responseEncoding;

    private Map<String, ? extends ServletContext> contexts;

    private ClassLoader classLoader;

    private Map<String, Object> attributes = new HashMap<>();

    private org.apache.logging.log4j.Logger logger = LogManager.getLogger("servlet.context.JerryServletContext");

    public JerryServletContext(Set<String> resourcePaths, Map<String, MimeType> mimeTypes, Map<String, ServletContext> contexts, ClassLoader classLoader){
        this.contextPath = "";
        this.contextParameters = new HashMap<>();
        this.servletRegistrations = new HashMap<>();
        this.filterRegistrations = new HashMap<>();
        this.sessionTimeout = 43200;
        this.resourcePaths = resourcePaths;
        this.mimeTypes = mimeTypes;
        this.contexts = contexts;
        this.classLoader = classLoader;
    }

    public JerryServletContext(Map<String, String> contextParameters,
                               Map<String, JerryServletRegistration> servletRegistrations,
                               Map<String, JerryFilterRegistration> filterRegistrations, Set<EventListener> listeners,
                               Set<String> resourcePaths, Map<String, MimeType> mimeTypes, Map<String, ? extends ServletContext> contexts, ClassLoader classLoader){
        this.contextPath = "";
        this.contextParameters = contextParameters;
        this.servletRegistrations = servletRegistrations;
        this.filterRegistrations = filterRegistrations;
        this.resourcePaths = resourcePaths;
        this.mimeTypes = mimeTypes;
        this.contexts = contexts;
        this.classLoader = classLoader;
        sortListeners(listeners);
    }

    public JerryServletContext(String contextPath, Map<String, String> contextParameters,
                               Map<String, JerryServletRegistration> servletRegistrations,
                               Map<String, JerryFilterRegistration> filterRegistrations, Set<EventListener> listeners,
                               Set<String> resourcePaths, Map<String, MimeType> mimeTypes, Map<String, ? extends ServletContext> contexts, ClassLoader classLoader){
        this.contextPath = contextPath;
        this.contextParameters = contextParameters;
        this.servletRegistrations = servletRegistrations;
        this.filterRegistrations = filterRegistrations;
        this.resourcePaths = resourcePaths;
        this.mimeTypes = mimeTypes;
        this.contexts = contexts;
        this.classLoader = classLoader;
        sortListeners(listeners);
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
        return 4;
    }

    public int getEffectiveMajorVersion() {
        return 4;
    }

    public int getEffectiveMinorVersion() {
        return 4;
    }

    public String getMimeType(String file) {
        return mimeTypes.get(file).toString();
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
        Pattern pattern = Pattern.compile(path + "(\\w*" + Pattern.quote(File.separator) + "| \\w*$)");
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
                if(path.equals(p)){
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
        return new JerryEnumeration<>();
    }

    public Enumeration<String> getServletNames() {
        return new JerryEnumeration<>();
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
        if(name == null) throw new NullPointerException();
        return contextParameters.get(name);
    }

    public Enumeration<String> getInitParameterNames() {
        return new Enumeration<String>() {

            Iterator<String> parameterNames = contextParameters.keySet().iterator();

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
        if(name == null) throw new NullPointerException();
        if(initialized) throw new IllegalStateException();

        boolean result = contextParameters.containsKey(name);
        contextParameters.put(name, value);
        return result;
    }

    public Object getAttribute(String name) {
        if(name == null) throw new NullPointerException();
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
        if(name == null) throw new NullPointerException();

        if (attributes.containsKey(name)){
            for (ServletContextAttributeListener listener : servletContextAttributeListeners){
                listener.attributeReplaced(new ServletContextAttributeEvent(this, name, object));
            }
        }else {
            for (ServletContextAttributeListener listener : servletContextAttributeListeners){
                listener.attributeAdded(new ServletContextAttributeEvent(this, name, object));
            }
        }

        attributes.put(name, object);
    }

    public void removeAttribute(String name) {
        for (ServletContextAttributeListener listener : servletContextAttributeListeners){
            listener.attributeRemoved(new ServletContextAttributeEvent(this, name, attributes.get(name)));
        }
        attributes.remove(name);
    }

    public String getServletContextName() {
        return null;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        check(servletName);

        if(servletRegistrations.get(servletName) != null) return null;

        JerryServletRegistrationDynamic servletRegistrationDynamic = new JerryServletRegistrationDynamic(servletName, className);
        servletRegistrations.put(servletName, servletRegistrationDynamic);
        return servletRegistrationDynamic;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        check(servletName);

        if(servlet instanceof SingleThreadModel){
            throw new IllegalArgumentException();
        }

        if(servletRegistrations.get(servletName) != null) return null;

        JerryServletRegistrationDynamic servletRegistrationDynamic = new JerryServletRegistrationDynamic(servletName, servlet);
        servletRegistrations.put(servletName, servletRegistrationDynamic);
        return servletRegistrationDynamic;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return addServlet(servletName, servletClass.getName());
    }

    public ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) {
        return null;
    }

    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        T servlet = null;
        try {
            servlet =  clazz.newInstance();
        } catch (InstantiationException e) {
            logger.error("Can' create servlet.");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return servlet;
    }

    public ServletRegistration getServletRegistration(String servletName) {
        return servletRegistrations.get(servletName);
    }

    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return servletRegistrations;
    }

    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        check(filterName);

        if(filterRegistrations.get(filterName) != null) return null;

        JerryFilterRegistrationDynamic filterRegistrationDynamic = new JerryFilterRegistrationDynamic(filterName, className);
        filterRegistrations.put(filterName, filterRegistrationDynamic);
        return filterRegistrationDynamic;
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return addFilter(filterName, filter.getClass().getName());
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return addFilter(filterName, filterClass.getName());
    }

    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        T filter = null;
        try {
            filter =  clazz.newInstance();
        } catch (InstantiationException e) {
            logger.error("Can' create servlet.");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return filter;
    }

    public FilterRegistration getFilterRegistration(String filterName) {
        return filterRegistrations.get(filterName);
    }

    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return filterRegistrations;
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
        if(initialized){
            throw new IllegalStateException();
        }

        try {
            Class<EventListener> listenerClass = (Class<EventListener>) classLoader.loadClass(className);
            if(! addListenerToSet(listenerClass.newInstance())){
                throw new IllegalArgumentException();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public <T extends EventListener> void addListener(T t) {
        if(initialized){
            throw new IllegalStateException();
        }

        if(! addListenerToSet(t)){
            throw new IllegalArgumentException();
        }
    }

    public void addListener(Class<? extends EventListener> listenerClass) {
        if(initialized){
            throw new IllegalStateException();
        }

        try {
            if(! addListenerToSet(listenerClass.newInstance())){
                throw new IllegalArgumentException();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        T listener = null;
        try {
            listener = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return listener;
    }

    public Set<ServletRequestAttributeListener> getRequestAttributeListeners() {
        return requestAttributeListeners;
    }

    public Set<HttpSessionAttributeListener> getSessionAttributeListeners() {
        return sessionAttributeListeners;
    }

    public Set<HttpSessionBindingListener> getSessionBindingListeners() {
        return sessionBindingListeners;
    }

    public Set<HttpSessionActivationListener> getSessionActivationListeners() {
        return sessionActivationListeners;
    }

    public Set<ServletContextAttributeListener> getServletContextAttributeListeners() {
        return servletContextAttributeListeners;
    }

    public Set<ServletContextListener> getServletContextListeners() {
        return servletContextListeners;
    }

    public Set<HttpSessionListener> getSessionListeners() {
        return sessionListeners;
    }

    public Set<ServletRequestListener> getRequestListeners() {
        return requestListeners;
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
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getRequestCharacterEncoding() {
        return requestEncoding;
    }

    public void setRequestCharacterEncoding(String encoding) {
        requestEncoding = encoding;
    }

    public String getResponseCharacterEncoding() {
        return responseEncoding;
    }

    public void setResponseCharacterEncoding(String encoding) {
        responseEncoding = encoding;
    }

    private void check(String... params){
        for(String param : params){
            if(param == null) throw new IllegalArgumentException();
            else if(param.isEmpty()) throw new IllegalArgumentException();
        }

        if(initialized) throw new IllegalStateException();
    }

    private void sortListeners(Set<EventListener> listeners){
        for (EventListener listener : listeners){
            addListenerToSet(listener);
        }
    }

    public void init(){

        for (ServletContextListener contextListener : servletContextListeners){
            contextListener.contextInitialized(new ServletContextEvent(this));
        }

        initialized = true;
    }

    public void destroy(){
        for (ServletContextListener contextListener : servletContextListeners){
            contextListener.contextDestroyed(new ServletContextEvent(this));
        }
    }

    public void setInitialized(boolean initialized){
        this.initialized = initialized;
    }

    private boolean addListenerToSet(EventListener listener){
            if (listener instanceof ServletRequestAttributeListener){
                requestAttributeListeners.add((ServletRequestAttributeListener) listener);
                return true;
            }
            else if(listener instanceof HttpSessionAttributeListener){
                sessionAttributeListeners.add((HttpSessionAttributeListener) listener);
                return true;
            }
            else if(listener instanceof HttpSessionBindingListener){
                sessionBindingListeners.add((HttpSessionBindingListener) listener);
                return true;
            }
            else if(listener instanceof HttpSessionActivationListener){
                sessionActivationListeners.add((HttpSessionActivationListener) listener);
                return true;
            }
            else if(listener instanceof ServletContextAttributeListener){
                servletContextAttributeListeners.add((ServletContextAttributeListener) listener);
                return true;
            }
            else if(listener instanceof ServletContextListener){
                servletContextListeners.add((ServletContextListener) listener);
                return true;
            }
            else if(listener instanceof HttpSessionListener){
                sessionListeners.add((HttpSessionListener) listener);
                return true;
            }
            else if(listener instanceof ServletRequestListener){
                requestListeners.add((ServletRequestListener) listener);
                return true;
            }
            return false;
    }
}
