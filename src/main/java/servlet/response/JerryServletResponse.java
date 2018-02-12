package servlet.response;

import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class JerryServletResponse implements ServletResponse {

    private HttpResponse response;
    private ServletContext servletContext;
    private String contentType;

    public JerryServletResponse(HttpResponse response, ServletContext servletContext){
        this.response = response;
        this.servletContext = servletContext;
    }

    @Override
    public String getCharacterEncoding() {
        return response.getFirstHeader("Accept-Charset").getValue();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        response.setHeader("Accept-Charset", charset);
    }

    @Override
    public void setContentLength(int len) {
        response.setHeader("Content-Length", String.valueOf(len));
    }

    @Override
    public void setContentLengthLong(long len) {
        response.setHeader("Content-Length", String.valueOf(len));
    }

    @Override
    public void setContentType(String type) {
        response.setHeader("Content-Type", type);
    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {
        response.setLocale(loc);
    }

    @Override
    public Locale getLocale() {
        HeaderElement element = response.getFirstHeader("Accept-Language").getElements()[0];
        int index = element.getName().indexOf("_");
        return new Locale(element.getName().substring(0, index), element.getName().substring(index, element.getName().length()));
    }
}
