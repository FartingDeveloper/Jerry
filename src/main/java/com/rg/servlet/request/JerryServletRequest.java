package com.rg.servlet.request;

import com.rg.http.core.HeaderElement;
import com.rg.http.io.HttpRequest;
import com.rg.servlet.JerryEnumeration;
import com.rg.servlet.context.JerryAsyncContext;
import com.rg.servlet.context.JerryServletContext;
import com.rg.servlet.io.JerryServletInputStream;
import com.rg.servlet.response.JerryServletResponse;

import javax.servlet.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class JerryServletRequest implements ServletRequest {

    protected HttpRequest request;
    private JerryServletInputStream servletInputStream;
    private BufferedReader bufferedReader;

    protected JerryServletResponse servletResponse;

    protected JerryServletContext servletContext;
    private Map<String, Object> attributes;
    private Set<ServletRequestAttributeListener> listeners;

    private boolean asyncStarted;

    private JerryAsyncContext asyncContext;

    public JerryServletRequest(HttpRequest request, JerryServletResponse servletResponse, JerryServletContext servletContext){
        this.request = request;
        this.servletInputStream = new JerryServletInputStream(request);

        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(request.getContentInputStream(), getCharacterEncoding()));
        } catch (UnsupportedEncodingException e) {
            bufferedReader = new BufferedReader(new InputStreamReader(request.getContentInputStream()));
        }

        this.servletResponse = servletResponse;
        this.servletContext = servletContext;
        attributes = new HashMap<>();
        listeners = servletContext.getRequestAttributeListeners();
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new JerryEnumeration<>(attributes.keySet().iterator());
    }

    @Override
    public String getCharacterEncoding() {
        if(request.getHeader("Accept-Charset") != null){
            return request.getHeader("Accept-Charset").getValue();
        }else{
            return "UTF-8";
        }
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        request.setHeader("Accept-Charset", env);
    }

    @Override
    public int getContentLength() {
        Integer length = Integer.valueOf(request.getHeader("Content-Length").getValue());
        if(length == null || length < 0){
            return -1;
        }
        return length;
    }

    @Override
    public long getContentLengthLong() {
        Long length = Long.valueOf(request.getHeader("Content-Length").getValue());
        if(length == null|| length < 0){
            return -1;
        }
        return length;
    }

    @Override
    public String getContentType() {
        return request.getHeader("Content-Type").getValue();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return servletInputStream;
    }

    @Override
    public String getParameter(String name) {
        return request.getRequestParameterByName(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return new JerryEnumeration<>(request.getRequestParameters().keySet().iterator());
    }

    @Override
    public String[] getParameterValues(String name) {
        return request.getRequestParameters().get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return request.getRequestParameters();
    }

    @Override
    public String getProtocol() {
        return request.getRequestLine().getProtocolVersion();
    }

    @Override
    public String getScheme() {
        String scheme = request.getRequestLine().getProtocolVersion();
        int index = scheme.indexOf("/");
        return scheme.substring(0, index).toLowerCase();
    }

    @Override
    public String getServerName() {
        String name = request.getHeader("Host").getValue();
        int index = name.lastIndexOf(":");
        if(index != -1){
            return name.substring(0, index);
        }
        return name;
    }

    @Override
    public int getServerPort() {
        String port = request.getHeader("Host").getValue();
        int index = port.lastIndexOf(":");
        if(index != -1){
            return Integer.valueOf(port.substring(index + 1, port.length()));
        }
        return -1;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return bufferedReader;
    }

    @Override
    public String getRemoteAddr() {
        for(HeaderElement element : request.getHeader("Forwarded").getElements()){
            String value = element.getParameterByName("by");
            if(value != null){
                if(value.contains(":")){
                    value = value.substring(0, value.indexOf(":"));
                }
                return value;
            }
        }
        return null;
    }

    @Override
    public String getRemoteHost() {
        for(HeaderElement element : request.getHeader("Forwarded").getElements()){
            String value = element.getParameterByName("host");
            if(value != null){
                return value;
            }
        }
        return getRemoteAddr();
    }

    @Override
    public void setAttribute(String name, Object o) {
        if(attributes.containsKey(name)){
            for (ServletRequestAttributeListener listener : listeners){
                listener.attributeReplaced(new ServletRequestAttributeEvent(servletContext, this, name, o));
            }
        } else{
            for (ServletRequestAttributeListener listener : listeners){
                listener.attributeAdded(new ServletRequestAttributeEvent(servletContext, this, name, o));
            }
        }
        attributes.put(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        for (ServletRequestAttributeListener listener : listeners){
            listener.attributeRemoved(new ServletRequestAttributeEvent(servletContext, this, name, attributes.get(name)));
        }
        attributes.remove(name);
    }

    @Override
    public Locale getLocale() {
        HeaderElement element = request.getHeader("Accept-Language").getElements().get(0);
        int index = element.getName().indexOf("_");
        String language = element.getName().substring(0, index);
        String region = element.getName().substring(index + 1, element.getName().length());
        return new Locale.Builder().setLanguage(language).setRegion(region).build();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        List<Locale> list = new ArrayList<>();
        for(HeaderElement element : request.getHeader("Accept-Language").getElements()){
            int index = element.getName().indexOf("_");
            String language = element.getName().substring(0, index);
            String region = element.getName().substring(index + 1, element.getName().length());
            list.add(new Locale.Builder().setLanguage(language).setRegion(region).build());
        }

        return new JerryEnumeration<>(list.iterator());
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return servletContext.getRequestDispatcher(path);
    }

    @Override
    public String getRealPath(String path) {
        return request.getHeader("Referer").getName();
    }

    public String getPath(){
        String uri = request.getRequestLine().getUri();
        int index = uri.indexOf("://");
        uri = uri.substring(index + 3, uri.length());
        index = uri.indexOf("/");
        int lastIndex = uri.indexOf("?");
        return uri.substring(index, lastIndex);
    }

    @Override
    public int getRemotePort() {
        HeaderElement element  = request.getHeader("Forwarded").getElements().get(0);
        String value = element.getParameterByName("by");
        if(value != null){
            int index = value.indexOf(":");
            if(index != -1){
                return Integer.valueOf(value.substring(index + 1, value.length()));
            }
        }
        return -1;
    }

    @Override
    public String getLocalName() {
        return getServerName();
    }

    @Override
    public String getLocalAddr() {
        for(HeaderElement element : request.getHeader("Forwarded").getElements()){
            String value = element.getParameterByName("for");
            if(value != null){
                return value;
            }
        }
        return null;
    }

    @Override
    public int getLocalPort() {
        return getServerPort();
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        startAsync(this, servletResponse);
        asyncContext.setOriginalRequestAndResponse(true);
        return asyncContext;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        if(asyncContext == null){
            asyncContext = new JerryAsyncContext();
            asyncContext.init((JerryServletRequest) servletRequest, (JerryServletResponse) servletResponse, getPath());
            asyncContext.setOriginalRequestAndResponse(false);
        }
        return asyncContext;
    }

    @Override
    public boolean isAsyncStarted() {
        return asyncStarted;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return asyncContext;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }
}
