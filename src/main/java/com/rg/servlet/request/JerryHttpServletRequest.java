package com.rg.servlet.request;

import com.rg.http.Header;
import com.rg.http.HeaderElement;
import com.rg.http.HttpRequest;
import com.rg.http.Syntax;
import com.rg.servlet.JerryEnumeration;
import com.rg.servlet.JerryHttpSession;
import com.rg.servlet.context.JerryServletContext;
import com.rg.servlet.response.JerryHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

public class JerryHttpServletRequest extends JerryServletRequest implements HttpServletRequest {

    private JerryHttpSession session;

    public JerryHttpServletRequest(HttpRequest request, JerryHttpServletResponse response , JerryServletContext servletContext) {
        super(request, response, servletContext);
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        Header header = request.getHeader("Cookie");
        if(header == null){
            return null;
        }

        List<HeaderElement> elements = header.getElements();
        Cookie[] cookies = new Cookie[elements.size()];

        int i = 0;
        for(HeaderElement element : elements){
            cookies[i++] = new Cookie(element.getName(), element.getValue());
        }
        return cookies;
    }

    @Override
    public long getDateHeader(String name) {
        Header header = request.getHeader("Date");
        String value = header.getValue();
        return LocalDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME).getLong(ChronoField.ERA);
    }

    @Override
    public String getHeader(String name) {
        return request.getHeader(name).getValue();
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        ArrayList<String> values = new ArrayList<>();
        for(Header header : request.getHeaders()){
            values.add(header.getValue());
        }
        return new JerryEnumeration<String>(values.iterator());
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        ArrayList<String> names = new ArrayList<>();
        for(Header header : request.getHeaders()){
            names.add(header.getName());
        }
        return new JerryEnumeration<String>(names.iterator());
    }

    @Override
    public int getIntHeader(String name) {
        Header header = request.getHeader(name);
        if(header == null){
            return 0;
        }
        return Integer.valueOf(header.getValue());
    }

    @Override
    public String getMethod() {
        return request.getRequestLine().getMethod();
    }

    @Override
    public String getPathInfo() {
        String path = getPath();
        int index = path.indexOf("/");
        path = path.substring(index + 1, path.length());
        index = path.indexOf("/");
        if(index == -1){
            return null;
        }
        return path.substring(index, path.length());
    }

    @Override
    public String getPathTranslated() {
        return servletContext.getRealPath(getPathInfo());
    }

    @Override
    public String getContextPath() {
        String uri = request.getRequestLine().getUri();
        int index = uri.indexOf("/");
        return uri.substring(index + 1, uri.length());
    }

    @Override
    public String getQueryString() {
        String uri = request.getRequestLine().getUri();
        int index = uri.indexOf(Syntax.PARAMS_START);
        if(index == -1){
            return null;
        }
        return uri.substring(index + 1, uri.length());
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return getPath();
    }

    @Override
    public StringBuffer getRequestURL() {
        String uri = request.getRequestLine().getUri();
        return new StringBuffer(uri.substring(0, uri.indexOf("?")));
    }

    @Override
    public String getServletPath() {
        String uri = getPath();
        while (uri.indexOf("/") != uri.lastIndexOf("/")){
            uri = uri.substring(0, uri.lastIndexOf("/"));
        }
        return uri;
    }

    @Override
    public HttpSession getSession(boolean create) {
        if(create && session == null){
            session = servletContext.createSession(servletResponse.getResponse());
        }
        return session;
    }

    public void setSession(JerryHttpSession session) {
        this.session = session;
    }

    @Override
    public HttpSession getSession() {
        if(session == null){
            session = servletContext.createSession(servletResponse.getResponse());
        }
        return session;
    }

    @Override
    public String changeSessionId() {
        servletContext.changeSessionId(session.getId());

        servletResponse.getResponse().setHeader("Set-Cookie", "JSESSIONID=" + session.getId());

        return session.getId();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        if(session == null){
            return false;
        }
        return true;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        for(HeaderElement element : request.getHeader("Cookie").getElements()){
            if(element.getName().equals("JSESSIONID")){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return request.getRequestLine().getUri().contains("JSESSIONID");
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return null;
    }
}
