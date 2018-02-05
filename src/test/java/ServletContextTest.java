import org.junit.Assert;
import org.junit.Test;

import javax.servlet.ServletException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Enumeration;

public class ServletContextTest extends WebXmlParserTest{

    @Test
    public void getContextPathTest(){
        if(! servletContext.getContextPath().equals("")){
            Assert.fail();
        }
    }

    @Test
    public void getContextTest(){

    }

    @Test
    public void getMimeTypeTest(){

    }

    @Test
    public void getResourcePathsTest(){

    }

    @Test
    public void getResourceTest(){

    }

    @Test
    public void getResourceAsStreamTest(){

    }

    @Test
    public void getRequestDispatcherTest(){
        if(servletContext.getRequestDispatcher("/login") == null){
            Assert.fail();
        }
    }

    @Test
    public void getNamedDispatcherTest(){
        if(servletContext.getNamedDispatcher("login") == null){
            Assert.fail();
        }
    }

    @Test
    public void getServletTest() throws ServletException {
        if(servletContext.getServlet("") != null){
            Assert.fail();
        }
    }

    @Test
    public void getServletsTest(){
        if(servletContext.getServlets() != null){
            Assert.fail();
        }
    }

    @Test
    public void getServletNamesTest(){
        if(servletContext.getServletNames().hasMoreElements() != false){
            Assert.fail();
        }
    }

    @Test
    public void getRealPathTest(){

    }

    @Test
    public void getInitParameterTest(){
        if(! servletContext.getInitParameter("springConfig").equals("config.xml")){
            Assert.fail();
        }
    }

    @Test(expected = NullPointerException.class)
    public void getNullInitParameterTest(){
        servletContext.getInitParameter(null);
    }

    @Test
    public void getInitParameterNamesTest(){
        Enumeration<String> enumeration = servletContext.getInitParameterNames();
        while (enumeration.hasMoreElements()){
            if (! enumeration.nextElement().equals("springConfig")){
                Assert.fail();
            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void setInitParameterTest(){
        String name = "Homer";
        String value = "Bart";
        servletContext.setInitParameter(name, value);
    }

    @Test(expected = NullPointerException.class)
    public void setNullInitParameterTest(){
        servletContext.setInitParameter(null, null);
    }

    @Test
    public void setAndGetAttributeTest(){
        String name = "Homer";
        String value = "Bart";
        servletContext.setAttribute(name, value);

        if (! servletContext.getAttribute(name).equals(value)){
            Assert.fail();
        }
    }

    @Test
    public void getAttributeNamesTest(){
        String[] names = {"Homer", "Marge"};
        String[] values = {"Bart", "Lisa"};

        for (int i = 0; i < names.length; i++){
            servletContext.setAttribute(names[i], values[i]);
        }

        Enumeration<String> namesEnum = servletContext.getAttributeNames();
        int j = 0;
        while (namesEnum.hasMoreElements()){
            if(! namesEnum.nextElement().equals(names[j++])){
                Assert.fail();
            }
        }

    }

    @Test(expected = NullPointerException.class)
    public void getNullAttributeTest(){
        servletContext.getAttribute(null);
    }

    @Test
    public void removeAttributeTest(){
        String name = "Homer";
        String value = "Bart";
        servletContext.setAttribute(name, value);
        servletContext.removeAttribute(name);
        if(servletContext.getAttribute(name) != null){
            Assert.fail();
        }
    }

}
