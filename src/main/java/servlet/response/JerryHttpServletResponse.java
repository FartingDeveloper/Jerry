package servlet.response;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class JerryHttpServletResponse extends JerryServletResponse implements HttpServletResponse{
    public JerryHttpServletResponse(HttpResponse response, ServletContext servletContext) {
        super(response, servletContext);
    }

    @Override
    public void addCookie(Cookie cookie) {
        response.setHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue());
    }

    @Override
    public boolean containsHeader(String name) {
        if(response.getFirstHeader(name) != null){
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
        if(isCommitted()){
            throw new IllegalStateException();
        }

        response.setHeader("Status", String.valueOf(sc) + " " + msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        if(isCommitted()){
            throw new IllegalStateException();
        }

        response.setHeader("Status", String.valueOf(sc));
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        response.setHeader("Location", location);
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
        Header header = response.getFirstHeader(name);
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
        setHeader("Status", String.valueOf(sc));
    }

    @Override
    public void setStatus(int sc, String sm) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getHeader(String name) {
        return response.getFirstHeader(name).getValue();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        ArrayList<String> list = new ArrayList<>();
        for (Header header: response.getHeaders(name)) {
            list.add(header.getValue());
        }
        return list;
    }

    @Override
    public Collection<String> getHeaderNames() {
        ArrayList<String> list = new ArrayList<>();
        for (Header header: response.getAllHeaders()) {
            list.add(header.getName());
        }
        return list;
    }
}
