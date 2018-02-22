package servlet;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.*;
import java.util.Enumeration;

public class ServletContextTest extends WebXmlParserTest {

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
        if(WebXmlParserTest.servletContext.getRequestDispatcher("/login") == null){
            Assert.fail();
        }
    }

    @Test
    public void getNamedDispatcherTest(){
        if(WebXmlParserTest.servletContext.getNamedDispatcher("login") == null){
            Assert.fail();
        }
    }

    @Test
    public void getServletTest() throws ServletException {
        if(WebXmlParserTest.servletContext.getServlet("") != null){
            Assert.fail();
        }
    }

    @Test
    public void getServletsTest(){
        if(WebXmlParserTest.servletContext.getServlets().hasMoreElements() != false){
            Assert.fail();
        }
    }

    @Test
    public void getServletNamesTest(){
        if(WebXmlParserTest.servletContext.getServletNames().hasMoreElements() != false){
            Assert.fail();
        }
    }

    @Test
    public void getRealPathTest(){

    }

    @Test
    public void getInitParameterTest(){
        if(! WebXmlParserTest.servletContext.getInitParameter("springConfig").equals("config.xml")){
            Assert.fail();
        }
    }

    @Test(expected = NullPointerException.class)
    public void getNullInitParameterTest(){
        WebXmlParserTest.servletContext.getInitParameter(null);
    }

    @Test
    public void getInitParameterNamesTest(){
        Enumeration<String> enumeration = WebXmlParserTest.servletContext.getInitParameterNames();
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
        WebXmlParserTest.servletContext.setInitParameter(name, value);
    }

    @Test(expected = NullPointerException.class)
    public void setNullInitParameterTest(){
        WebXmlParserTest.servletContext.setInitParameter(null, null);
    }

    @Test
    public void setAndGetAttributeTest(){
        String name = "Homer";
        String value = "Bart";
        WebXmlParserTest.servletContext.setAttribute(name, value);

        if (! WebXmlParserTest.servletContext.getAttribute(name).equals(value)){
            Assert.fail();
        }
    }

    @Test(expected = NullPointerException.class)
    public void setNullAttributeTest(){
        String name = null;
        String value = "Bart";
        WebXmlParserTest.servletContext.setAttribute(name, value);
    }

    @Test
    public void getAttributeNamesTest(){
        String[] names = {"Homer", "Marge"};
        String[] values = {"Bart", "Lisa"};

        for (int i = 0; i < names.length; i++){
            WebXmlParserTest.servletContext.setAttribute(names[i], values[i]);
        }

        Enumeration<String> namesEnum = WebXmlParserTest.servletContext.getAttributeNames();

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
        WebXmlParserTest.servletContext.getAttribute(null);
    }

    @Test
    public void removeAttributeTest(){
        String name = "Homer";
        String value = "Bart";
        WebXmlParserTest.servletContext.setAttribute(name, value);
        WebXmlParserTest.servletContext.removeAttribute(name);
        if(WebXmlParserTest.servletContext.getAttribute(name) != null){
            Assert.fail();
        }
    }


    @Test
    public void addServletWithNameAndClassNameTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "Homer";
        String servletClass = "Homer.class";
        ServletRegistration servletRegistration = WebXmlParserTest.servletContext.addServlet(name, servletClass);
        if(WebXmlParserTest.servletContext.getServletRegistration(name) != servletRegistration){
            Assert.fail();
        }
    }

    @Test
    public void addServletWithRepeatedNameAndClassNameTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "Homer";
        String servletClass = "Homer.class";
        WebXmlParserTest.servletContext.addServlet(name, servletClass);
        if(WebXmlParserTest.servletContext.addServlet(name, servletClass) != null){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithNullNameAndClassNameTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = null;
        String servletClass = "Homer.class";
        WebXmlParserTest.servletContext.addServlet(name, servletClass);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithEmptyNameAndClassNameTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "";
        String servletClass = "Homer.class";
        WebXmlParserTest.servletContext.addServlet(name, servletClass);
    }

    @Test(expected = IllegalStateException.class)
    public void addServletWithNameAndClassNameToInitializedContextTest(){
        String name = "Homer";
        String servletClass = "Homer.class";
        WebXmlParserTest.servletContext.addServlet(name, servletClass);
    }

    @Test
    public void addServletWithNameAndServletTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "Homer";
        Servlet servlet = Mockito.mock(Servlet.class);
        ServletRegistration servletRegistration = WebXmlParserTest.servletContext.addServlet(name, servlet);
        if(WebXmlParserTest.servletContext.getServletRegistration(name) != servletRegistration){
            Assert.fail();
        }
    }

    @Test
    public void addServletWithRepeatedNameAndServletTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "Homer";
        Servlet servlet = Mockito.mock(Servlet.class);
        WebXmlParserTest.servletContext.addServlet(name, servlet);
        if(WebXmlParserTest.servletContext.addServlet(name, servlet) != null){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithNullNameAndServletTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = null;
        Servlet servlet = null;
        WebXmlParserTest.servletContext.addServlet(name, servlet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithEmptyNameAnServletTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "";
        Servlet servlet = null;
        WebXmlParserTest.servletContext.addServlet(name, servlet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithNameAndSingleThreadModelServletTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "Homer";
        Servlet servlet = Mockito.mock(Servlet.class, Mockito.withSettings().extraInterfaces(SingleThreadModel.class));

        WebXmlParserTest.servletContext.addServlet(name, servlet);
    }

    @Test(expected = IllegalStateException.class)
    public void addServletWithNameAndServletToInitializedContextTest(){
        String name = "Homer";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        WebXmlParserTest.servletContext.addServlet(name, servletClass);
    }

    @Test
    public void addServletWithNameAndClassTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "Homer";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        WebXmlParserTest.servletContext.addServlet(name, servletClass);
        if(WebXmlParserTest.servletContext.getServletRegistration(name) == null){
            Assert.fail();
        }
    }

    @Test
    public void addServletWithRepeatedNameAndClassTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "Homer";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        WebXmlParserTest.servletContext.addServlet(name, servletClass);
        if(WebXmlParserTest.servletContext.addServlet(name, servletClass) != null){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithNullNameAndClassTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = null;
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        WebXmlParserTest.servletContext.addServlet(name, servletClass);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithEmptyNameAndClassTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        WebXmlParserTest.servletContext.addServlet(name, servletClass);
    }

    @Test(expected = IllegalStateException.class)
    public void addServletWithNameAndClassToInitializedContextTest(){
        String name = "Homer";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        WebXmlParserTest.servletContext.addServlet(name, servletClass);
    }

    @Test
    public void addFilterWithNameAndClassNameTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "Homer";
        String filterClass = "Homer.class";
        FilterRegistration filterRegistration = WebXmlParserTest.servletContext.addFilter(name, filterClass);
        if(WebXmlParserTest.servletContext.getFilterRegistration(name) != filterRegistration){
            Assert.fail();
        }
    }

    @Test
    public void addFilterWithRepeatedNameAndClassNameTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "Homer";
        String filterClass = "Homer.class";
        WebXmlParserTest.servletContext.addFilter(name, filterClass);
        if(WebXmlParserTest.servletContext.addFilter(name, filterClass) != null){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFilterWithNullNameAndClassNameTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = null;
        String filterClass = "Homer.class";
        WebXmlParserTest.servletContext.addFilter(name, filterClass);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFilterWithEmptyNameAndClassNameTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "";
        String filterClass = "Homer.class";
        WebXmlParserTest.servletContext.addFilter(name, filterClass);
    }

    @Test(expected = IllegalStateException.class)
    public void addFilterWithNameAndClassNameToInitializedContextTest(){
        String name = "Homer";
        String filterClass = "Homer.class";
        WebXmlParserTest.servletContext.addFilter(name, filterClass);
    }

    @Test
    public void addFilterWithNameAndFilterTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "Homer";
        Filter filter = Mockito.mock(Filter.class);
        FilterRegistration filterRegistration = WebXmlParserTest.servletContext.addFilter(name, filter);
        if(WebXmlParserTest.servletContext.getFilterRegistration(name) != filterRegistration){
            Assert.fail();
        }
    }

    @Test
    public void addFilterWithRepeatedNameAndFilterTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "Homer";
        Filter filter = Mockito.mock(Filter.class);
        WebXmlParserTest.servletContext.addFilter(name, filter);
        if(WebXmlParserTest.servletContext.addFilter(name, filter) != null){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFilterWithNullNameAndFilterTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = null;
        Filter filter = Mockito.mock(Filter.class);
        WebXmlParserTest.servletContext.addFilter(name, filter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFilterWithEmptyNameAndFilterTest(){
        WebXmlParserTest.servletContext.setInitialized(false);

        String name = "";
        Filter filter = Mockito.mock(Filter.class);
        WebXmlParserTest.servletContext.addFilter(name, filter);
    }

    @Test(expected = IllegalStateException.class)
    public void addFilterWithNameAndFilterToInitializedContextTest(){
        String name = "Homer";
        Filter filter = Mockito.mock(Filter.class);
        WebXmlParserTest.servletContext.addFilter(name, filter);
    }

    @Test
    public void createServletTest() throws ServletException {
        if(WebXmlParserTest.servletContext.createServlet(Mockito.mock(Servlet.class).getClass()) == null){
            Assert.fail();
        }
    }

    @Test
    public void createFilterTest() throws ServletException {
        if(WebXmlParserTest.servletContext.createFilter(Mockito.mock(Filter.class).getClass()) == null){
            Assert.fail();
        }
    }
}
