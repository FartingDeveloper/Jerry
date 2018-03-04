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

    private JerryServletOutputStream contentOutputStream;
    private PrintWriter contentWriter;

    private boolean contentOutputStreamIsCalled;
    private boolean contentWriterIsCalled;

    public JerryServletResponse(HttpResponse response, ServletContext servletContext){
        this.response = response;
        this.servletContext = servletContext;
        contentOutputStream = new JerryServletOutputStream(response);
        contentWriter = new PrintWriter(contentOutputStream);
    }

    @Override
    public String getCharacterEncoding() {
        Header header = response.getHeader("Content-Type");
        if(header != null){
            for(HeaderElement element : header.getElements()){
                String param = element.getParameterByName("charset");
                if(param != null){
                    return param;
                }
            }
        }
        return null;
    }

    @Override
    public String getContentType() {
        Header header = response.getHeader("Content-Type");
        if(header != null){
            response.getHeader("Content-Type").getValue();
        }
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if(contentWriterIsCalled){
            throw new IllegalStateException();
        }
        contentOutputStreamIsCalled = true;
        return contentOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if(getCharacterEncoding() == null){
            throw new UnsupportedEncodingException();
        }
        if(contentOutputStreamIsCalled){
            throw new IllegalStateException();
        }
        contentWriterIsCalled = true;
        return contentWriter;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        checkCommit();
        if(response.getHeader("Content-Type") != null){
            response.setHeader("Content-Type", response.getHeader("Content-Type").getName() + Syntax.ELEMENT_PARAMS_SEPARATOR + "charset=" + charset);
        }
        else{
            response.setHeader("Content-Type", "text/html" + Syntax.ELEMENT_PARAMS_SEPARATOR + "charset=" + charset);
        }
    }

    @Override
    public void setContentLength(int len) {
        checkCommit();
        response.setHeader("Content-Length", String.valueOf(len));
    }

    @Override
    public void setContentLengthLong(long len) {
        checkCommit();
        response.setHeader("Content-Length", String.valueOf(len));
    }

    @Override
    public void setContentType(String type) {
        checkCommit();
        response.setHeader("Content-Type", type);
    }

    @Override
    public void setBufferSize(int size) {
        checkCommit();
        contentOutputStream.setBufferSize(size);
    }

    @Override
    public int getBufferSize() {
        return contentOutputStream.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
        contentOutputStream.flush();
    }

    @Override
    public void resetBuffer() {
        contentOutputStream.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        return response.isCommited();
    }

    @Override
    public void reset() {
        checkCommit();
        contentOutputStream.resetBuffer();
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

    protected void checkCommit(){
        if(isCommitted()){
            throw new IllegalStateException();
        }
    }
}
