package servlet.response;

import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import servlet.io.JerryServletOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class JerryServletResponse implements ServletResponse {

    protected HttpResponse response;
    protected ServletContext servletContext;

    private JerryServletOutputStream outputStream;
    private PrintWriter writer;

    private boolean commited;
    private byte[] buffer;
    private int bufferSize;

    public JerryServletResponse(HttpResponse response, ServletContext servletContext){
        this.response = response;
        this.servletContext = servletContext;
        outputStream = new JerryServletOutputStream(null);
        writer = new PrintWriter(outputStream);
    }

    @Override
    public String getCharacterEncoding() {
        return response.getFirstHeader("Accept-Charset").getValue();
    }

    @Override
    public String getContentType() {
        return response.getFirstHeader("Content-Type").getValue();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return writer;
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
        outputStream.setBufferSize(size);
    }

    @Override
    public int getBufferSize() {
        return outputStream.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
        outputStream.flush();
    }

    @Override
    public void resetBuffer() {
        outputStream.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        return commited;
    }

    public void commit(){
        commited = true;
    }

    @Override
    public void reset() {
        if(commited){
            throw new IllegalStateException();
        }
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

    public HttpResponse getResponse() {
        return response;
    }
}
