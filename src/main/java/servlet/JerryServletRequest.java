package servlet;

import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.servlet.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

public class JerryServletRequest implements ServletRequest {

    private HttpRequest request;
    private ServletContext servletContext;
    private Map<String, Object> attributes;
    private List<ServletRequestAttributeListener> listeners;

    public JerryServletRequest(HttpRequest request, ServletContext servletContext){
        this.request = request;
        this.servletContext = servletContext;
        attributes = new HashMap<>();
        listeners = new ArrayList<>();
    }

    public JerryServletRequest(HttpRequest request, ServletContext servletContext, List<ServletRequestAttributeListener> listeners){
        this.request = request;
        this.servletContext = servletContext;
        attributes = new HashMap<>();
        this.listeners = listeners;
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
        return request.getFirstHeader("Accept-Encoding").getName();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        request.setHeader("Accept-Encoding", env);
    }

    @Override
    public int getContentLength() {
        Integer length = Integer.valueOf(request.getFirstHeader("Content-Length").getName());
        if(length == null || length < 0){
            return -1;
        }
        return length;
    }

    @Override
    public long getContentLengthLong() {
        Long length = Long.valueOf(request.getFirstHeader("Content-Length").getName());
        if(length == null){
            return -1;
        }
        return length;
    }

    @Override
    public String getContentType() {
        return request.getFirstHeader("Content-Type").getName();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String name) {
        String parameter = null;
        try {
            List<NameValuePair> parameters = URLEncodedUtils.parse(new URI(
                    request.getRequestLine().getUri()), Charset.forName(getCharacterEncoding()));
            for (NameValuePair nameValuePair : parameters) {
                if(nameValuePair.getName().equals(name)){
                    parameter = nameValuePair.getValue();
                    break;
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return parameter;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        JerryEnumeration<String> result = null;
        try {
            List<NameValuePair> parameters = URLEncodedUtils.parse(new URI(
                    request.getRequestLine().getUri()), Charset.forName(getCharacterEncoding()));
            if(parameters != null){
                List<String> list = new ArrayList<>();
                for (NameValuePair parameter : parameters){
                    list.add(parameter.getValue());
                }
                result = new JerryEnumeration<>(list.iterator());
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] result = null;
        try {
            List<NameValuePair> parameters = URLEncodedUtils.parse(new URI(
                    request.getRequestLine().getUri()), Charset.forName(getCharacterEncoding()));
            if(parameters != null){
                List<String> list = new ArrayList<>();
                for (NameValuePair parameter : parameters){
                    list.add(parameter.getName());
                }
                result = list.toArray(new String[list.size()]);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> result = new HashMap();
        try {
            List<NameValuePair> parameters = URLEncodedUtils.parse(new URI(
                    request.getRequestLine().getUri()), Charset.forName(getCharacterEncoding()));
            if(parameters != null){
                for (NameValuePair parameter : parameters){
                    if(result.containsKey(parameter.getName())){
                        String[] values = result.get(parameter.getName());
                        String[] newValues = new String[values.length + 1];
                        for (int i = 0; i < values.length; i++){
                            newValues[i] = values[i];
                        }
                        newValues[newValues.length] = parameter.getValue();
                        result.put(parameter.getName(), newValues);
                    }
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String getProtocol() {
        return request.getProtocolVersion().getProtocol();
    }

    @Override
    public String getScheme() {
        for(HeaderElement element : request.getFirstHeader("Forwarded").getElements()){
            NameValuePair param = element.getParameterByName("proto");
            if(param != null){
                return param.getValue();
            }
        }
        return null;
    }

    @Override
    public String getServerName() {
        return request.getFirstHeader("Host").getName();
    }

    @Override
    public int getServerPort() {
        return Integer.valueOf(request.getFirstHeader("Host").getValue());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        for(HeaderElement element : request.getFirstHeader("Forwarded").getElements()){
            NameValuePair param = element.getParameterByName("by");
            if(param != null){
                return param.getValue();
            }
        }
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
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
        String locale = request.getFirstHeader("Accept-Language").getValue();
        if(locale == null){
            return Locale.getDefault();
        }
        return Locale.forLanguageTag(locale);
    }

    @Override
    public Enumeration<Locale> getLocales() {
        List<Locale> list = new ArrayList<>();
        for(HeaderElement element : request.getFirstHeader("Accept-Language").getElements()){
            list.add(Locale.forLanguageTag(element.getName()));
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
        return request.getFirstHeader("Referer").getName();
    }

    @Override
    public int getRemotePort() {
        for(HeaderElement element : request.getFirstHeader("Forwarded").getElements()){
            NameValuePair param = element.getParameterByName("by");
            if(param != null){
                return Integer.valueOf(param.getValue());
            }
        }
        return 0;
    }

    @Override
    public String getLocalName() {
        return getServerName();
    }

    @Override
    public String getLocalAddr() {
        for(HeaderElement element : request.getFirstHeader("Forwarded").getElements()){
            NameValuePair param = element.getParameterByName("for");
            if(param != null){
                return param.getValue();
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
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }
}
