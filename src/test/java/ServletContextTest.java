import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.*;
import java.util.Enumeration;

public class ServletContextTest extends WebXmlParserTest{

    @Test
    public void getContextPathTest(){

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
        if(servletContext.getServlets().hasMoreElements() != false){
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

    @Test(expected = NullPointerException.class)
    public void setNullAttributeTest(){
        String name = null;
        String value = "Bart";
        servletContext.setAttribute(name, value);
    }

    @Test
    public void getAttributeNamesTest(){
        String[] names = {"Homer", "Marge"};
        String[] values = {"Bart", "Lisa"};

        for (int i = 0; i < names.length; i++){
            servletContext.setAttribute(names[i], values[i]);
        }

        Enumeration<String> namesEnum = servletContext.getAttributeNames();

        while (namesEnum.hasMoreElements()){
            boolean result = false;
            String name = namesEnum.nextElement();
            for (int i = 0; i < names.length; i++){
                if(names[i].equals(name)){
                    result = true;
                }
            }
            if(! result){
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


    @Test
    public void addServletWithNameAndClassNameTest(){
        servletContext.setInitialized(false);

        String name = "Homer";
        String servletClass = "Homer.class";
        ServletRegistration servletRegistration = servletContext.addServlet(name, servletClass);
        if(servletContext.getServletRegistration(name) != servletRegistration){
            Assert.fail();
        }
    }

    @Test
    public void addServletWithRepeatedNameAndClassNameTest(){
        servletContext.setInitialized(false);

        String name = "Homer";
        String servletClass = "Homer.class";
        servletContext.addServlet(name, servletClass);
        if(servletContext.addServlet(name, servletClass) != null){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithNullNameAndClassNameTest(){
        servletContext.setInitialized(false);

        String name = null;
        String servletClass = "Homer.class";
        servletContext.addServlet(name, servletClass);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithEmptyNameAndClassNameTest(){
        servletContext.setInitialized(false);

        String name = "";
        String servletClass = "Homer.class";
        servletContext.addServlet(name, servletClass);
    }

    @Test(expected = IllegalStateException.class)
    public void addServletWithNameAndClassNameToInitializedContextTest(){
        String name = "Homer";
        String servletClass = "Homer.class";
        servletContext.addServlet(name, servletClass);
    }

    @Test
    public void addServletWithNameAndServletTest(){
        servletContext.setInitialized(false);

        String name = "Homer";
        Servlet servlet = Mockito.mock(Servlet.class);
        ServletRegistration servletRegistration = servletContext.addServlet(name, servlet);
        if(servletContext.getServletRegistration(name) != servletRegistration){
            Assert.fail();
        }
    }

    @Test
    public void addServletWithRepeatedNameAndServletTest(){
        servletContext.setInitialized(false);

        String name = "Homer";
        Servlet servlet = Mockito.mock(Servlet.class);
        servletContext.addServlet(name, servlet);
        if(servletContext.addServlet(name, servlet) != null){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithNullNameAndServletTest(){
        servletContext.setInitialized(false);

        String name = null;
        Servlet servlet = null;
        servletContext.addServlet(name, servlet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithEmptyNameAnServletTest(){
        servletContext.setInitialized(false);

        String name = "";
        Servlet servlet = null;
        servletContext.addServlet(name, servlet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithNameAndSingleThreadModelServletTest(){
        servletContext.setInitialized(false);

        String name = "Homer";
        Servlet servlet = Mockito.mock(Servlet.class, Mockito.withSettings().extraInterfaces(SingleThreadModel.class));

        servletContext.addServlet(name, servlet);
    }

    @Test(expected = IllegalStateException.class)
    public void addServletWithNameAndServletToInitializedContextTest(){
        String name = "Homer";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        servletContext.addServlet(name, servletClass);
    }

    @Test
    public void addServletWithNameAndClassTest(){
        servletContext.setInitialized(false);

        String name = "Homer";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        servletContext.addServlet(name, servletClass);
        if(servletContext.getServletRegistration(name) == null){
            Assert.fail();
        }
    }

    @Test
    public void addServletWithRepeatedNameAndClassTest(){
        servletContext.setInitialized(false);

        String name = "Homer";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        servletContext.addServlet(name, servletClass);
        if(servletContext.addServlet(name, servletClass) != null){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithNullNameAndClassTest(){
        servletContext.setInitialized(false);

        String name = null;
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        servletContext.addServlet(name, servletClass);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithEmptyNameAndClassTest(){
        servletContext.setInitialized(false);

        String name = "";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        servletContext.addServlet(name, servletClass);
    }

    @Test(expected = IllegalStateException.class)
    public void addServletWithNameAndClassToInitializedContextTest(){
        String name = "Homer";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        servletContext.addServlet(name, servletClass);
    }
}
