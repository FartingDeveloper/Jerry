package com.rg.servlet.response;

import com.rg.http.core.Header;
import com.rg.http.core.HeaderElement;
import com.rg.http.io.HttpResponse;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class JerryHttpServletResponse extends JerryServletResponse implements HttpServletResponse{

    public JerryHttpServletResponse(HttpResponse response, ServletContext servletContext) {
        super(response, servletContext);
        response.setHeader("Content-Type", "text/html");
    }

    @Override
    public void addCookie(Cookie cookie) {
        response.setHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue());
    }

    @Override
    public boolean containsHeader(String name) {
        if(response.getHeader(name) != null){
            return true;
        }
        return false;
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeRedirectUrl(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        checkCommit();
        response.setStatus(sc, msg);
        response.flush();
    }

    @Override
    public void sendError(int sc) throws IOException {
        checkCommit();
        response.setStatusCode(sc);
        response.flush();
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        checkCommit();
        response.setStatusCode(SC_FOUND);
        response.setHeader("Location", location);
        response.flush();
    }

    @Override
    public void setDateHeader(String name, long date) {
        setHeader(name, String.valueOf(date));
    }

    @Override
    public void addDateHeader(String name, long date) {
        addHeader(name, String.valueOf(date));
    }

    @Override
    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        Header header = response.getHeader(name);
        if(header != null){
            response.setHeader(name, header.getValue() + "," + value);
        }
        else{
            setHeader(name, value);
        }
    }

    @Override
    public void setIntHeader(String name, int value) {
        setHeader(name, String.valueOf(value));
    }

    @Override
    public void addIntHeader(String name, int value) {
        addHeader(name, String.valueOf(value));
    }

    @Override
    public void setStatus(int sc) {
        response.setStatusCode(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {
        response.setStatus(sc, sm);
    }

    @Override
    public int getStatus() {
        return response.getStatusCode();
    }

    @Override
    public String getHeader(String name) {
        return response.getHeader(name).getValue();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        ArrayList<String> list = new ArrayList<>();
        Header header = response.getHeader(name);
        for(HeaderElement element : header.getElements()){
                list.add(element.getName());
        }
        return list;
    }

    @Override
    public Collection<String> getHeaderNames() {
        ArrayList<String> list = new ArrayList<>();
        for (Header header: response.getHeaders()) {
            list.add(header.getName());
        }
        return list;
    }
}
