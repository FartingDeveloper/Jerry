package servlet;

import com.sun.scenario.effect.impl.prism.PrImage;
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
        return response.getFirstHeader("Accept-Encoding").getName();
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
        response.setHeader("Accept-Encoding", charset);
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

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
