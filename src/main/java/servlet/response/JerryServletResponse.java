package servlet.response;

import http.Header;
import http.HeaderElement;
import http.HttpResponse;
import http.Syntax;
import servlet.io.JerryServletOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class JerryServletResponse implements ServletResponse {

    protected HttpResponse response;
    protected ServletContext servletContext;

    private JerryServletOutputStream outputStream;
    private PrintWriter writer;

    private boolean commited;
    private boolean contentOutputStreamIsCalled;
    private boolean contentWriterIsCalled;

    public JerryServletResponse(HttpResponse response, ServletContext servletContext){
        this.response = response;
        this.servletContext = servletContext;
        outputStream = new JerryServletOutputStream(response);
        writer = new PrintWriter(outputStream);
    }

    @Override
    public String getCharacterEncoding() {
        Header header = response.getHeader("Content-Type");
        HeaderElement element = header.getElements().get(0);
        return element.getParameterByName("charset");
    }

    @Override
    public String getContentType() {
        return response.getHeader("Content-Type").getValue();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if(checkEncoding()){
            throw new UnsupportedEncodingException();
        }
        if(contentWriterIsCalled){
            throw new IllegalStateException();
        }
        contentOutputStreamIsCalled = true;
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if(checkEncoding()){
            throw new UnsupportedEncodingException();
        }
        if(contentOutputStreamIsCalled){
            throw new IllegalStateException();
        }
        contentWriterIsCalled = true;
        return writer;
    }

    private boolean checkEncoding(){
        if(getCharacterEncoding() == null){
            return true;
        }
        return false;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        if(commited){
            throw new IllegalStateException();
        }
        response.setHeader("Content-Type", response.getHeader("Content-Type").getName() + Syntax.ELEMENT_PARAMS_SEPARATOR + "charset=" + charset);
    }

    @Override
    public void setContentLength(int len) {
        if(commited){
            throw new IllegalStateException();
        }
        response.setHeader("Content-Length", String.valueOf(len));
    }

    @Override
    public void setContentLengthLong(long len) {
        if(commited){
            throw new IllegalStateException();
        }
        response.setHeader("Content-Length", String.valueOf(len));
    }

    @Override
    public void setContentType(String type) {
        if(commited){
            throw new IllegalStateException();
        }
        response.setHeader("Content-Type", type);
    }

    @Override
    public void setBufferSize(int size) {
        if(commited){
            throw new IllegalStateException();
        }
        outputStream.setBufferSize(size);
    }

    @Override
    public int getBufferSize() {
        return outputStream.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
        commit();
        outputStream.flush();
    }

    @Override
    public void resetBuffer() {
        outputStream.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        return commited || outputStream.isFlushed();
    }

    public void commit(){
        commited = true;
    }

    @Override
    public void reset() {
        if(commited){
            throw new IllegalStateException();
        }
        outputStream.resetBuffer();
    }

    @Override
    public void setLocale(Locale loc) {
        response.setHeader("Content-Language", loc.toLanguageTag());
    }

    @Override
    public Locale getLocale() {
        HeaderElement element = response.getHeader("Content-Language").getElements().get(0);
        return new Locale(element.getName());
    }

    public HttpResponse getResponse() {
        return response;
    }
}
