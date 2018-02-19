
import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import servlet.JerryHttpSession;
import servlet.context.JerryServletContext;
import servlet.request.JerryHttpServletRequest;
import servlet.response.JerryHttpServletResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingListener;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpServletRequestTest extends ServletRequestTest {

    JerryHttpServletRequest httpServletRequest;
    JerryServletContext servletContext;
    JerryHttpServletResponse servletResponse;

    @Before
    public void init() throws URISyntaxException {
        super.init();

        servletContext = mock(JerryServletContext.class);
        servletResponse = mock(JerryHttpServletResponse.class);
        httpServletRequest = new JerryHttpServletRequest(httpGet, servletResponse, servletContext);
    }

    @Test
    public void getCookiesTest(){

        StringBuilder value = new StringBuilder();
        for(int i = 0; i < names.length; i++){
            value.append(names[i] + "=" + values[i]);
            if(i != names.length - 1){
                value.append(",");
            }
        }
        httpGet.setHeader("Cookie", value.toString());

        Cookie[] cookies = httpServletRequest.getCookies();

        for(int i = 0; i < cookies.length; i++){
            if(!cookies[i].equals(names[i]) && !cookies[i].getValue().equals(values[i])){
                Assert.fail();
            }
        }
    }

    @Test
    public void getNullCookiesTest(){
       if(httpServletRequest.getCookies() != null){
           Assert.fail();
       }
    }

    @Test
    public void getDateHeaderTest(){
        String date = "Tue, 15 Nov 1994 08:12:31 GMT";

        long l = LocalDateTime.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME).getLong(ChronoField.ERA);
        httpGet.setHeader("Date", "Tue, 15 Nov 1994 08:12:31 GMT");

        if(l != httpServletRequest.getDateHeader(date)){
            Assert.fail();
        }
    }

    @Test
    public void getHeaderTest(){
        if(! httpServletRequest.getHeader("Accept-Charset").equals(UTF_8)){
            Assert.fail();
        }
    }

    @Test
    public void getHeadersTest(){
        httpGet.setHeader("Accept-Language", Locale.getDefault().toString() + "," + Locale.JAPAN.toString());
        httpGet.setHeader("Accept-Language", Locale.getDefault().toString() + "," + Locale.ITALIAN.toString());

        Enumeration<String> enumeration = httpServletRequest.getHeaders("Accept-Language");
        while (enumeration.hasMoreElements()){
            System.out.println(enumeration.nextElement());
        }
    }

    @Test
    public void getIntHeaderTest(){
        int value = 12;
        httpGet.setHeader("Age", String.valueOf(value));
        if(httpServletRequest.getIntHeader("Age") != 12){
            Assert.fail();
        }
    }

    @Test
    public void getMethodTest(){
        if(! httpServletRequest.getMethod().equals("GET")){
            Assert.fail();
        }
    }

    @Test
    public void getPathInfoTest(){
        if(! httpServletRequest.getPathInfo().equals("/lala/lala")){
            Assert.fail();
        }
    }

    @Test
    public void getQueryStringTest(){
        if(! httpServletRequest.getQueryString().equals(names[0] + "=" + values[0] + "&" + names[1] + "=" + values[1])){
            Assert.fail();
        }
    }

    @Test
    public void getRequestUriTest(){
        if(!httpServletRequest.getRequestURI().equals("/servlet/lala/lala")){
            Assert.fail();
        }
    }

    @Test
    public void getRequestUrlTest(){

        String uri = httpGet.getRequestLine().getUri();
        uri = uri.substring(0, uri.indexOf("?"));

        if(!httpServletRequest.getRequestURL().toString().equals(uri)){
            Assert.fail();
        }
    }

    @Test
    public void getServletPathTest(){
        if(! httpServletRequest.getServletPath().equals("/servlet")){
            Assert.fail();
        }
    }
}
