package com.rg.servlet;

import com.rg.http.HttpParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import com.rg.servlet.response.JerryHttpServletResponse;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import java.io.IOException;

public class HttpServletResponseTest extends ServletResponseTest {

    public JerryHttpServletResponse httpServletResponse;

    @Before
    public void init() throws IOException, HttpParser.WrongRequestException {
        super.init();
        httpServletResponse = new JerryHttpServletResponse(response, Mockito.mock(ServletContext.class));
    }

    @Test
    public void addCookieTest(){
        String name1 = "Hello";
        String value1 = "there";
        httpServletResponse.addCookie(new Cookie(name1, value1));

        if(! httpServletResponse.getHeader("Set-Cookie").equals(name1 + "=" + value1)) {
            Assert.fail();
        }
    }

    @Test
    public void containsHeaderTest(){
        String name = "Hello";
        String value = "there";
        httpServletResponse.setHeader(name, value);

        if(! httpServletResponse.containsHeader(name)){
            Assert.fail();
        }
    }

    @Test
    public void sendErrorTest() throws IOException {
        httpServletResponse.sendError(404, "Ooops");
    }

    @Test(expected = IllegalStateException.class)
    public void sendErrorWhenResponseIsCommitedTest() throws IOException {
        httpServletResponse.getOutputStream().write(HTML.getBytes());
        httpServletResponse.flushBuffer();

        httpServletResponse.sendError(404, "Ooops");
    }

    @Test
    public void senRedirectTest() throws IOException {
        httpServletResponse.sendRedirect("https://www.google.com/");
    }

    @Test(expected = IllegalStateException.class)
    public void senRedirectWhenResponseIsCommitedTest() throws IOException {
        httpServletResponse.getOutputStream().write(HTML.getBytes());
        httpServletResponse.flushBuffer();

        httpServletResponse.sendRedirect("https://www.google.com/");
    }

    @Test
    public void addHeaderTest(){
        String name = "Hello";
        String value1 = "there";
        httpServletResponse.addHeader(name, value1);

        String value2 = "there";
        httpServletResponse.addHeader(name, value2);

        if(! response.getHeader(name).getValue().equals(value1 +  "," + value2)){
            Assert.fail();
        }
    }

    @Test
    public void setHeaderTest(){
        String name = "Hello";
        String value1 = "there";
        httpServletResponse.addHeader(name, value1);

        String value2 = "there";
        httpServletResponse.setHeader(name, value2);

        if(! response.getHeader(name).getValue().equals(value2)){
            Assert.fail();
        }
    }

    @Test
    public void getHeadersTest(){
        String name = "Hello";
        String value = "FART";
        httpServletResponse.addHeader(name, value);

        boolean result = false;

        for(String header : httpServletResponse.getHeaders(name)){
            if(header.equals(value)){
                result = true;
            }
        }

        if(! result){
            Assert.fail();
        }
    }

    @Test
    public void getHeadersNamesTest(){
        String name = "Hello";
        String value = "FART";
        httpServletResponse.addHeader(name, value);

        boolean result = false;

        for(String header : httpServletResponse.getHeaderNames()){
            if(header.equals(name)){
                result = true;
            }
        }

        if(! result){
            Assert.fail();
        }
    }

}
