import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import servlet.context.JerryServletContext;
import servlet.request.JerryServletRequest;

import javax.servlet.ServletContext;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public class ServletRequestTest {

    public static final String UTF_8 = "UTF-8";
    public static final String UTF_16 = "UTF-16";

    public static final String[] names = {"Homer", "Marge"};
    public static final String[] values = {"Bart", "Lisa", "Maggy"};

    public static final String SCHEME = "http";
    public static final String HOST = "www.pornhub.com";

    public static final int PORT = 80;
    
    HttpGet httpGet;
    JerryServletRequest servletRequest;

    @Before
    public void init() throws URISyntaxException {
        JerryServletContext context = mock(JerryServletContext.class);
        
        URIBuilder builder = new URIBuilder();
        builder.setScheme(SCHEME).setHost(HOST).setPath("/search").setCharset(Charset.forName(UTF_8));
        for (int i = 0; i < names.length; i++){
            builder.setParameter(names[i], values[i]);
        }
        URI uri = builder.build();

        httpGet = new HttpGet(uri);
        httpGet.setHeader("Host", HOST + ":" + PORT);
        httpGet.setHeader("Accept-Charset", UTF_8);
        httpGet.setHeader("Accept-Language", Locale.getDefault().toString() + "," + Locale.CANADA.toString());
        httpGet.setHeader("Forwarded", "for=127.0.0.1;proto=http;by=127.0.0.1:" + PORT);

        servletRequest = new JerryServletRequest(httpGet, context);
    }

    @Test
    public void addAttributeTest(){
        servletRequest.setAttribute(names[0], values[0]);
        if(servletRequest.getAttribute(names[0]) != values[0]){
            Assert.fail();
        }
    }

    @Test
    public void getAttributeNamesTest(){
        for (int i = 0; i < names.length; i++){
            servletRequest.setAttribute(names[i], values[i]);
        }

        Enumeration<String> attributeNames = servletRequest.getAttributeNames();

        while (attributeNames.hasMoreElements()){
            boolean result = false;
            String attributeName = attributeNames.nextElement();
            for (int i = 0; i < names.length; i++){
                if(attributeName.equals(names[i])){
                    result = true;
                }
            }
            if(! result){
                Assert.fail();
            }
        }
    }

    @Test
    public void setCharacterEncodingTest() throws UnsupportedEncodingException {
        servletRequest.setCharacterEncoding("ASCII");
        if (! servletRequest.getCharacterEncoding().equals("ASCII")){
            Assert.fail();
        }
    }

    @Test
    public void getContentLenghtTest(){
        String contentLenght = "Content-Length";
        int lenght = 121;

        HttpRequest request = mock(HttpRequest.class);

        when(request.getFirstHeader(contentLenght)).thenReturn(mock(Header.class));
        when(request.getFirstHeader(contentLenght).getValue()).thenReturn(String.valueOf(lenght));

        servletRequest = new JerryServletRequest(request, null);
        if(servletRequest.getContentLength() != lenght){
            Assert.fail();
        }
    }

    @Test
    public void setContentLenghtGreaterThanIntMaxTest(){
        String contentLenght = "Content-Length";
        int lenght = Integer.MAX_VALUE + 1;

        HttpRequest request = mock(HttpRequest.class);

        when(request.getFirstHeader(contentLenght)).thenReturn(mock(Header.class));
        when(request.getFirstHeader(contentLenght).getValue()).thenReturn(String.valueOf(lenght));

        servletRequest = new JerryServletRequest(request, null);
        if(servletRequest.getContentLength() != -1){
            Assert.fail();
        }
    }

    @Test
    public void getContentLenghtLongTest(){
        String contentLenght = "Content-Length";
        long lenght = 121L;

        HttpRequest request = mock(HttpRequest.class);

        when(request.getFirstHeader(contentLenght)).thenReturn(mock(Header.class));
        when(request.getFirstHeader(contentLenght).getValue()).thenReturn(String.valueOf(lenght));

        servletRequest = new JerryServletRequest(request, null);
        if(servletRequest.getContentLength() != lenght){
            Assert.fail();
        }
    }

    @Test
    public void getContentTypeTest(){
        String content = "Content-Type";
        String contentTypeValue = "application/x-www-form-urlencoded";

        HttpRequest request = mock(HttpRequest.class);

        when(request.getFirstHeader(content)).thenReturn(mock(Header.class));
        when(request.getFirstHeader(content).getValue()).thenReturn(contentTypeValue);

        servletRequest = new JerryServletRequest(request, null);
        if(servletRequest.getContentType() != contentTypeValue){
            Assert.fail();
        }
    }

    @Test
    public void getParameterTest() throws URISyntaxException {
        if (! servletRequest.getParameter(names[0]).equals(values[0])){
            Assert.fail();
        }
    }

    @Test
    public void getParameterNamesTest() throws URISyntaxException {
       Enumeration<String> paramNames = servletRequest.getParameterNames();

       while (paramNames.hasMoreElements()){
           String element = paramNames.nextElement();
           boolean result = false;
           for (int i = 0; i < names.length; i++){
               if(element.equals(names[i])){
                   result = true;
               }
           }
           if(! result){
               Assert.fail();
           }
       }
    }

    @Test
    public void getParameterValuesTest() throws URISyntaxException {
        String[] paramValues = servletRequest.getParameterValues(names[0]);

        for(String value : paramValues){
            if(! value.equals(values[0])){
                Assert.fail();
            }
        }
    }

    @Test
    public void getParameterMapTest() throws URISyntaxException {
        Map<String, String[]> map = servletRequest.getParameterMap();

        for(String value : map.keySet()){
            String[] arr = map.get(value);
            int count = 0;
            for (int i = 0; i < arr.length; i++){
                for (int j = 0; j < names.length; j++){
                    if(arr[i].equals(values[j])){
                        count++;
                    }
                }
            }
            if(count != arr.length){
                Assert.fail();
            }
        }
    }

    @Test
    public void getProtocolTest(){
        if(! servletRequest.getProtocol().equals(httpGet.getProtocolVersion().toString())){
            Assert.fail();
        }
    }

    @Test
    public void getSchemeTest(){
        if(! servletRequest.getScheme().equals(SCHEME)){
            Assert.fail();
        }
    }

    @Test
    public void getServerNameTest(){
        if(! servletRequest.getServerName().equals(HOST)){
            Assert.fail();
        }
    }

    @Test
    public void getServerPortTest(){
        if(servletRequest.getServerPort() != PORT){
            Assert.fail();
        }
    }

    @Test
    public void getRemoteAddrTest(){
        if(! servletRequest.getRemoteAddr().equals("127.0.0.1")){
            Assert.fail();
        }
    }

    @Test
    public void getRemoteHostTest(){
        if(! servletRequest.getRemoteHost().equals("127.0.0.1")){
            Assert.fail();
        }
    }

    @Test
    public void getLocaleTest(){
        if(! servletRequest.getLocale().getLanguage().equals(Locale.getDefault().getLanguage())){
            Assert.fail();
        }
    }

    @Test
    public void getRemotePortTest(){
        if(servletRequest.getRemotePort() != PORT){
            Assert.fail();
        }
    }
}
