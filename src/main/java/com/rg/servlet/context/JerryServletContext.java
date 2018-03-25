package com.rg.servlet.context;

import com.rg.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import com.rg.servlet.JerryEnumeration;
import com.rg.servlet.JerryHttpSession;
import com.rg.servlet.registration.JerryDynamicRequestDispatcher;
import com.rg.servlet.registration.JerryFilterRegistration;
import com.rg.servlet.registration.JerryServletRegistration;
import com.rg.servlet.registration.JerryStaticRequestDispatcher;

import javax.activation.MimeType;
import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class JerryServletContext implements ServletContext {

    private boolean initialized;

    private String contextPath;
    private String contextName;

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

    private ClassLoader classLoader;

    private Map<String, ? extends ServletContext> contexts;

    private Map<String, JerryHttpSession> sessions;

    private Map<String, Object> attributes = new HashMap<>();

    private org.apache.logging.log4j.Logger logger = LogManager.getLogger("com.rg.servlet.context.JerryServletContext");

    public JerryServletContext(Map<String, JerryServletContext> contexts, Set<String> resourcePaths, ClassLoader classLoader){
        this.contexts = contexts;
        this.resourcePaths = resourcePaths;
        this.classLoader = classLoader;

        this.contextPath = "/";
        this.contextParameters = new HashMap<>();
        this.servletRegistrations = new HashMap<>();
        this.filterRegistrations = new HashMap<>();
        this.mimeTypes = new HashMap<>();
        this.sessions = new HashMap<>();
        this.sessionTimeout = 43200;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
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

        path = contextName + path;

        for(String resource : resourcePaths){
            if(isPartOfDir(path, resource)){
                int begin = resource.lastIndexOf(contextName);
                int end = resource.indexOf(path) + path.length();
                end = resource.indexOf("/", end);
                if(end == -1){
                    resource = resource.substring(begin + contextName.length(), resource.length());
                } else{
                    resource = resource.substring(begin + contextName.length(), end);
                }
                paths.add(resource);
            }
        }

        if (path.isEmpty()){
            return null;
        }
        return paths;
    }

    private boolean isPartOfDir(String path, String resource){
        Pattern pattern = Pattern.compile(Pattern.quote(path) + "\\w+");
        if(pattern.matcher(resource).find()){
            return true;
        }
        return false;
    }

    public URL getResource(String path) throws MalformedURLException {
        URL url = null;
        for(String resource : resourcePaths){
            if (comparePath(path, resource)){
                url = new URL(resource);
                break;
            }
        }
        return url;
    }

    private boolean comparePath(String path, String resource){
        Pattern pattern = Pattern.compile(path + "$");
        return pattern.matcher(resource).find();
    }

    public InputStream getResourceAsStream(String path) {
        InputStream in = null;
        try {
            File file = new File(getResource(path).toURI());
            in = new FileInputStream(file);
        } catch (MalformedURLException e) {
            logger.error("Wrong resource name.", e);
        } catch (FileNotFoundException e) {
            logger.error("File isn't found.", e);
        } catch (URISyntaxException e) {
            logger.error("URI syntax problem.", e);
        }
        return in;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        for (JerryServletRegistration servletRegistration : servletRegistrations.values()){
            for (String p : servletRegistration.getMappings()){
                if(path.equals(p)){
                    Servlet servlet = servletRegistration.getServlet();
                    return new JerryDynamicRequestDispatcher(servlet);
                }
            }
        }

        for(String p : resourcePaths){
            int index = p.indexOf(contextName);
            p = p.substring(index + contextName.length(), p.length());
            if(path.equals(p)){
                try {
                    return new JerryStaticRequestDispatcher(new URL(p));
                } catch (MalformedURLException e) {
                    logger.error("URL problem.", e);
                }
            }
        }

        return null;
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        for (JerryServletRegistration servletRegistration : servletRegistrations.values()){
            if(servletRegistration.getName().equals(name)){
                Servlet servlet = servletRegistration.getServlet();
                return new JerryDynamicRequestDispatcher(servlet);
            }
        }
        return null;
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

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getServletContextName() {
        return contextName;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        check(servletName);

        JerryServletRegistration servletRegistration;
        servletRegistration = servletRegistrations.get(servletName);
        if(servletRegistration != null){
            servletRegistration.setClassName(className);
            return servletRegistration;
        }

        servletRegistration = new JerryServletRegistration(servletName, className);

        servletRegistrations.put(servletName, servletRegistration);
        return servletRegistration;
    }

    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        check(servletName);

        JerryServletRegistration servletRegistration;
        servletRegistration = servletRegistrations.get(servletName);
        if(servletRegistration != null){
            servletRegistration.setClassName(servlet.getClass().getName());
            servletRegistration.setServlet(servlet);
            return servletRegistration;
        }

        servletRegistration = new JerryServletRegistration(servletName, servlet.getClass().getName());

        servletRegistrations.put(servletName, servletRegistration);
        return servletRegistration;
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
            logger.error("Can' create com.rg.servlet.", e);
        } catch (IllegalAccessException e) {
            logger.error("Can' create com.rg.servlet.", e);
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

        JerryFilterRegistration filterRegistration = filterRegistrations.get(filterName);
        if(filterRegistrations.get(filterName) != null){
            filterRegistrations.get(filterName).setClassName(className);
            return filterRegistration;
        }

        filterRegistration = new JerryFilterRegistration(filterName, className);
        filterRegistrations.put(filterName, filterRegistration);
        return filterRegistration;
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        check(filterName);

        JerryFilterRegistration filterRegistration = filterRegistrations.get(filterName);
        if(filterRegistrations.get(filterName) != null){
            filterRegistrations.get(filterName).setClassName(filter.getClass().getName());
            filterRegistration.setFilter(filter);
            return filterRegistration;
        }

        filterRegistration = new JerryFilterRegistration(filterName, filter.getClass().getName());
        filterRegistration.setFilter(filter);
        filterRegistrations.put(filterName, filterRegistration);
        return filterRegistration;
    }

    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return addFilter(filterName, filterClass.getName());
    }

    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        T filter = null;
        try {
            filter =  clazz.newInstance();
        } catch (InstantiationException e) {
            logger.error("Can' create com.rg.servlet.", e);
        } catch (IllegalAccessException e) {
            logger.error("Can' create com.rg.servlet.", e);
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
            logger.error("Class not found.", e);
        } catch (IllegalAccessException e) {
            logger.error("Can' create com.rg.servlet.", e);
        } catch (InstantiationException e) {
            logger.error("Can' create com.rg.servlet.", e);
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

    public void addServletRegistration(JerryServletRegistration registration){
        servletRegistrations.put(registration.getName(), registration);
    }

    public void addFilterRegistration(JerryFilterRegistration registration){
        filterRegistrations.put(registration.getName(), registration);
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

    public void init() throws ClassNotFoundException, InstantiationException, ServletException, IllegalAccessException {

        if(initialized){
            throw new IllegalStateException();
        }

        for (ServletContextListener contextListener : servletContextListeners){
            contextListener.contextInitialized(new ServletContextEvent(this));
        }

        for(JerryServletRegistration servletRegistration : servletRegistrations.values()){
            servletRegistration.init(this);
        }

        for(JerryFilterRegistration filterRegistration : filterRegistrations.values()){
            filterRegistration.init(this);
        }

        initialized = true;
    }

    public void destroy(){
        for (ServletContextListener contextListener : servletContextListeners){
            contextListener.contextDestroyed(new ServletContextEvent(this));
        }
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

    public JerryHttpSession createSession(HttpResponse response){
        JerryHttpSession session = new JerryHttpSession(this);
        session.setMaxInactiveInterval(sessionTimeout);
        sessions.put(session.getId(), session);

        response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId());

        for (HttpSessionListener listener : sessionListeners){
            listener.sessionCreated(new HttpSessionEvent(session));
        }

        return session;
    }


    public void changeSessionId(String sessionId){
        JerryHttpSession session = sessions.get(sessionId);
        if(session == null) {
            throw new IllegalStateException();
        }
        session.setId(UUID.randomUUID().toString());
        sessions.remove(sessionId);
        sessions.put(session.getId(), session);
    }

    public void destroySession(String sessionId){
        for (HttpSessionListener listener : sessionListeners){
            listener.sessionDestroyed(new HttpSessionEvent(sessions.get(sessionId)));
        }

        sessions.remove(sessionId);
    }

    public JerryHttpSession getSession(String sessionId){
        return sessions.get(sessionId);
    }
}
