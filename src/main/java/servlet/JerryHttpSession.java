package servlet;

import servlet.context.JerryServletContext;

import javax.servlet.ServletContext;
import javax.servlet.http.*;
import java.time.LocalTime;
import java.util.*;

public class JerryHttpSession implements HttpSession {

    private JerryServletContext servletContext;

    private String id;
    private long creationTime;
    private long lastAccesedTime;
    private int invalidateTime;
    private boolean invalidate;

    private Map<String, Object> attributes = new Hashtable<>();

    private Set<HttpSessionAttributeListener> sessionAttributeListeners;
    private Set<HttpSessionBindingListener> sessionBindingListeners;

    public JerryHttpSession(JerryServletContext context){
        servletContext = context;
        sessionAttributeListeners = context.getSessionAttributeListeners();
        sessionBindingListeners = context.getSessionBindingListeners();
        creationTime = LocalTime.now().toNanoOfDay();
        id = UUID.randomUUID().toString();
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccesedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        invalidateTime = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return invalidateTime;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Object getValue(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new JerryEnumeration<>(attributes.keySet().iterator());
    }

    @Override
    public String[] getValueNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(String name, Object value) {
        if(value == null){
            removeAttribute(name);
        }

        if(attributes.containsKey(name)){
            for (HttpSessionAttributeListener listener : sessionAttributeListeners){
                listener.attributeAdded(new HttpSessionBindingEvent(this, name, value));
            }
        } else{
            for (HttpSessionAttributeListener listener : sessionAttributeListeners){
                listener.attributeReplaced(new HttpSessionBindingEvent(this, name, value));
            }
        }

        for (HttpSessionBindingListener listener : sessionBindingListeners){
            listener.valueBound(new HttpSessionBindingEvent(this, name, value));
        }

        attributes.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttribute(String name) {
        for (HttpSessionAttributeListener listener : sessionAttributeListeners){
            listener.attributeRemoved(new HttpSessionBindingEvent(this, name));
        }
        for (HttpSessionBindingListener listener : sessionBindingListeners){
            listener.valueUnbound(new HttpSessionBindingEvent(this, name));
        }
        attributes.remove(name);
    }

    @Override
    public void removeValue(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidate() {
        servletContext.destroySession(id);
        invalidate = true;
    }

    @Override
    public boolean isNew() {
        if(invalidate){
            throw new IllegalStateException();
        }
        return false;
    }

    public void setLastAccesedTime(long lastAccesedTime) {
        this.lastAccesedTime = lastAccesedTime;
    }
}
