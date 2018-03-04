package servlet;

import http.HttpParser;
import http.HttpResponseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import servlet.response.JerryServletResponse;


import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ServletResponseTest extends HttpResponseTest{

    public JerryServletResponse servletResponse;

    @Before
    public void init() throws IOException, HttpParser.WrongRequestException {
        super.init();
        servletResponse = new JerryServletResponse(response, Mockito.mock(ServletContext.class));
    }

    @Test
    public void getOutputStreamTest() throws IOException {
        servletResponse.getOutputStream().write(HTML.getBytes());
        servletResponse.getOutputStream().flush();
        if(! servletResponse.isCommitted()){
            Assert.fail();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void contentOutputStreamIsCalledTest() throws IOException, HttpParser.WrongRequestException, InterruptedException {
        servletResponse.getOutputStream().write(HTML.getBytes());
        servletResponse.setCharacterEncoding("utf-8");
        servletResponse.getWriter().write(HTML);
    }

    @Test
    public void getWriterTest() throws IOException {
        servletResponse.setCharacterEncoding("utf-8");
        servletResponse.getWriter().write(HTML);
        servletResponse.getWriter().flush();
        if(! servletResponse.isCommitted()){
            Assert.fail();
        }
    }

    @Test(expected = UnsupportedEncodingException.class)
    public void unsupportedEncodingExceptionTest() throws IOException, HttpParser.WrongRequestException, InterruptedException {
        servletResponse.getWriter().write(HTML);
    }

    @Test(expected = IllegalStateException.class)
    public void contentWriterIsCalled() throws IOException {
        servletResponse.setCharacterEncoding("utf-8");
        servletResponse.getWriter();
        servletResponse.getOutputStream();
    }

    @Test(expected = IllegalStateException.class)
    public void setBufferSizeTest() throws IOException {
        servletResponse.getOutputStream().write(HTML.getBytes());
        servletResponse.getOutputStream().flush();
        servletResponse.setBufferSize(10);
    }

}
