package servlet.request;

import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import servlet.JerryEnumeration;
import servlet.context.JerryAsyncContext;
import servlet.context.JerryServletContext;
import servlet.response.JerryServletResponse;

import javax.servlet.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

public class JerryServletRequest implements ServletRequest {

    protected HttpRequest request;
    protected JerryServletResponse servletResponse;

    protected JerryServletContext servletContext;
    private Map<String, Object> attributes;
    private Set<ServletRequestAttributeListener> listeners;

    private boolean asyncStarted;

    private JerryAsyncContext asyncContext;

    public JerryServletRequest(HttpRequest request, JerryServletResponse servletResponse, JerryServletContext servletContext){
        this.request = request;
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
        return request.getFirstHeader("Accept-Charset").getValue();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        request.setHeader("Accept-Charset", env);
    }

    @Override
    public int getContentLength() {
        Integer length = Integer.valueOf(request.getFirstHeader("Content-Length").getValue());
        if(length == null || length < 0){
            return -1;
        }
        return length;
    }

    @Override
    public long getContentLengthLong() {
        Long length = Long.valueOf(request.getFirstHeader("Content-Length").getValue());
        if(length == null|| length < 0){
            return -1;
        }
        return length;
    }

    @Override
    public String getContentType() {
        return request.getFirstHeader("Content-Type").getValue();
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
                    list.add(parameter.getName());
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
                    if(parameter.getName().equals(name)){
                        list.add(parameter.getValue());
                    }
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
        return request.getProtocolVersion().toString();
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
        String name = request.getFirstHeader("Host").getValue();
        int index = name.lastIndexOf(":");
        if(index != -1){
            return name.substring(0, index);
        }
        return name;
    }

    @Override
    public int getServerPort() {
        String port = request.getFirstHeader("Host").getValue();
        int index = port.lastIndexOf(":");
        if(index != -1){
            return Integer.valueOf(port.substring(index + 1, port.length()));
        }
        return Integer.valueOf(port);
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
        HeaderElement element = request.getFirstHeader("Accept-Language").getElements()[0];
        int index = element.getName().indexOf("_");
        return new Locale(element.getName().substring(0, index), element.getName().substring(index, element.getName().length()));
    }

    @Override
    public Enumeration<Locale> getLocales() {
        List<Locale> list = new ArrayList<>();
        for(HeaderElement element : request.getFirstHeader("Accept-Language").getElements()){
            int index = element.getName().indexOf("_");
            list.add(new Locale(element.getName().substring(0, index), element.getName().substring(index + 1, element.getName().length())));
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
        HeaderElement element  = request.getFirstHeader("Forwarded").getElements()[0];
        NameValuePair param = element.getParameterByName("by");
        if(param != null){
            int index = param.getValue().indexOf(":");
            if(index != -1){
                return Integer.valueOf(param.getValue().substring(index + 1, param.getValue().length()));
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
