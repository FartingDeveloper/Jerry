package servlet;

import loader.ContextLoader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import servlet.context.JerryServletContext;

import javax.servlet.*;
import java.util.Enumeration;

public class ServletContextTest extends ContextLoaderTest {

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
        if(jerryServletContext.getRequestDispatcher("/login") == null){
            Assert.fail();
        }
    }

    @Test
    public void getNamedDispatcherTest(){
        if(jerryServletContext.getNamedDispatcher("login") == null){
            Assert.fail();
        }
    }

    @Test
    public void getServletTest() throws ServletException {
        if(jerryServletContext.getServlet("") != null){
            Assert.fail();
        }
    }

    @Test
    public void getServletsTest(){
        if(jerryServletContext.getServlets().hasMoreElements() != false){
            Assert.fail();
        }
    }

    @Test
    public void getServletNamesTest(){
        if(jerryServletContext.getServletNames().hasMoreElements() != false){
            Assert.fail();
        }
    }

    @Test
    public void getRealPathTest(){

    }

    @Test
    public void getInitParameterTest(){
        if(! jerryServletContext.getInitParameter("springConfig").equals("config.xml")){
            Assert.fail();
        }
    }

    @Test(expected = NullPointerException.class)
    public void getNullInitParameterTest(){
        jerryServletContext.getInitParameter(null);
    }

    @Test
    public void getInitParameterNamesTest(){
        Enumeration<String> enumeration = jerryServletContext.getInitParameterNames();
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
        jerryServletContext.setInitParameter(name, value);
    }

    @Test(expected = NullPointerException.class)
    public void setNullInitParameterTest(){
        jerryServletContext.setInitParameter(null, null);
    }

    @Test
    public void setAndGetAttributeTest(){
        String name = "Homer";
        String value = "Bart";
        jerryServletContext.setAttribute(name, value);

        if (! jerryServletContext.getAttribute(name).equals(value)){
            Assert.fail();
        }
    }

    @Test(expected = NullPointerException.class)
    public void setNullAttributeTest(){
        String name = null;
        String value = "Bart";
        jerryServletContext.setAttribute(name, value);
    }

    @Test
    public void getAttributeNamesTest(){
        String[] names = {"Homer", "Marge"};
        String[] values = {"Bart", "Lisa"};

        for (int i = 0; i < names.length; i++){
            jerryServletContext.setAttribute(names[i], values[i]);
        }

        Enumeration<String> namesEnum = jerryServletContext.getAttributeNames();

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
        jerryServletContext.getAttribute(null);
    }

    @Test
    public void removeAttributeTest(){
        String name = "Homer";
        String value = "Bart";
        jerryServletContext.setAttribute(name, value);
        jerryServletContext.removeAttribute(name);
        if(jerryServletContext.getAttribute(name) != null){
            Assert.fail();
        }
    }


    @Test
    public void addServletWithNameAndClassNameTest(){
        jerryServletContext.setInitialized(false);

        String name = "Homer";
        String servletClass = "Homer.class";
        ServletRegistration servletRegistration = jerryServletContext.addServlet(name, servletClass);
        if(jerryServletContext.getServletRegistration(name) != servletRegistration){
            Assert.fail();
        }
    }

    @Test
    public void addServletWithRepeatedNameAndClassNameTest(){
        jerryServletContext.setInitialized(false);

        String name = "Homer";
        String servletClass = "Homer.class";
        jerryServletContext.addServlet(name, servletClass);
        if(jerryServletContext.addServlet(name, servletClass) != null){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithNullNameAndClassNameTest(){
        jerryServletContext.setInitialized(false);

        String name = null;
        String servletClass = "Homer.class";
        jerryServletContext.addServlet(name, servletClass);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithEmptyNameAndClassNameTest(){
        jerryServletContext.setInitialized(false);

        String name = "";
        String servletClass = "Homer.class";
        jerryServletContext.addServlet(name, servletClass);
    }

    @Test(expected = IllegalStateException.class)
    public void addServletWithNameAndClassNameToInitializedContextTest(){
        String name = "Homer";
        String servletClass = "Homer.class";
        jerryServletContext.addServlet(name, servletClass);
    }

    @Test
    public void addServletWithNameAndServletTest(){
        jerryServletContext.setInitialized(false);

        String name = "Homer";
        Servlet servlet = Mockito.mock(Servlet.class);
        ServletRegistration servletRegistration = jerryServletContext.addServlet(name, servlet);
        if(jerryServletContext.getServletRegistration(name) != servletRegistration){
            Assert.fail();
        }
    }

    @Test
    public void addServletWithRepeatedNameAndServletTest(){
        jerryServletContext.setInitialized(false);

        String name = "Homer";
        Servlet servlet = Mockito.mock(Servlet.class);
        jerryServletContext.addServlet(name, servlet);
        if(jerryServletContext.addServlet(name, servlet) != null){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithNullNameAndServletTest(){
        jerryServletContext.setInitialized(false);

        String name = null;
        Servlet servlet = null;
        jerryServletContext.addServlet(name, servlet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithEmptyNameAnServletTest(){
        jerryServletContext.setInitialized(false);

        String name = "";
        Servlet servlet = null;
        jerryServletContext.addServlet(name, servlet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithNameAndSingleThreadModelServletTest(){
        jerryServletContext.setInitialized(false);

        String name = "Homer";
        Servlet servlet = Mockito.mock(Servlet.class, Mockito.withSettings().extraInterfaces(SingleThreadModel.class));

        jerryServletContext.addServlet(name, servlet);
    }

    @Test(expected = IllegalStateException.class)
    public void addServletWithNameAndServletToInitializedContextTest(){
        String name = "Homer";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        jerryServletContext.addServlet(name, servletClass);
    }

    @Test
    public void addServletWithNameAndClassTest(){
        jerryServletContext.setInitialized(false);

        String name = "Homer";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        jerryServletContext.addServlet(name, servletClass);
        if(jerryServletContext.getServletRegistration(name) == null){
            Assert.fail();
        }
    }

    @Test
    public void addServletWithRepeatedNameAndClassTest(){
        jerryServletContext.setInitialized(false);

        String name = "Homer";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        jerryServletContext.addServlet(name, servletClass);
        if(jerryServletContext.addServlet(name, servletClass) != null){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithNullNameAndClassTest(){
        jerryServletContext.setInitialized(false);

        String name = null;
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        jerryServletContext.addServlet(name, servletClass);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addServletWithEmptyNameAndClassTest(){
        jerryServletContext.setInitialized(false);

        String name = "";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        jerryServletContext.addServlet(name, servletClass);
    }

    @Test(expected = IllegalStateException.class)
    public void addServletWithNameAndClassToInitializedContextTest(){
        String name = "Homer";
        Class<? extends Servlet> servletClass = Mockito.mock(Servlet.class).getClass();
        jerryServletContext.addServlet(name, servletClass);
    }

    @Test
    public void addFilterWithNameAndClassNameTest(){
        jerryServletContext.setInitialized(false);

        String name = "Homer";
        String filterClass = "Homer.class";
        FilterRegistration filterRegistration = jerryServletContext.addFilter(name, filterClass);
        if(jerryServletContext.getFilterRegistration(name) != filterRegistration){
            Assert.fail();
        }
    }

    @Test
    public void addFilterWithRepeatedNameAndClassNameTest(){
        jerryServletContext.setInitialized(false);

        String name = "Homer";
        String filterClass = "Homer.class";
        jerryServletContext.addFilter(name, filterClass);
        if(jerryServletContext.addFilter(name, filterClass) != null){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFilterWithNullNameAndClassNameTest(){
        jerryServletContext.setInitialized(false);

        String name = null;
        String filterClass = "Homer.class";
        jerryServletContext.addFilter(name, filterClass);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFilterWithEmptyNameAndClassNameTest(){
        jerryServletContext.setInitialized(false);

        String name = "";
        String filterClass = "Homer.class";
        jerryServletContext.addFilter(name, filterClass);
    }

    @Test(expected = IllegalStateException.class)
    public void addFilterWithNameAndClassNameToInitializedContextTest(){
        String name = "Homer";
        String filterClass = "Homer.class";
        jerryServletContext.addFilter(name, filterClass);
    }

    @Test
    public void addFilterWithNameAndFilterTest(){
        jerryServletContext.setInitialized(false);

        String name = "Homer";
        Filter filter = Mockito.mock(Filter.class);
        FilterRegistration filterRegistration = jerryServletContext.addFilter(name, filter);
        if(jerryServletContext.getFilterRegistration(name) != filterRegistration){
            Assert.fail();
        }
    }

    @Test
    public void addFilterWithRepeatedNameAndFilterTest(){
        jerryServletContext.setInitialized(false);

        String name = "Homer";
        Filter filter = Mockito.mock(Filter.class);
        jerryServletContext.addFilter(name, filter);
        if(jerryServletContext.addFilter(name, filter) != null){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFilterWithNullNameAndFilterTest(){
        jerryServletContext.setInitialized(false);

        String name = null;
        Filter filter = Mockito.mock(Filter.class);
        jerryServletContext.addFilter(name, filter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFilterWithEmptyNameAndFilterTest(){
        jerryServletContext.setInitialized(false);

        String name = "";
        Filter filter = Mockito.mock(Filter.class);
        jerryServletContext.addFilter(name, filter);
    }

    @Test(expected = IllegalStateException.class)
    public void addFilterWithNameAndFilterToInitializedContextTest(){
        String name = "Homer";
        Filter filter = Mockito.mock(Filter.class);
        jerryServletContext.addFilter(name, filter);
    }

    @Test
    public void createServletTest() throws ServletException {
        if(jerryServletContext.createServlet(Mockito.mock(Servlet.class).getClass()) == null){
            Assert.fail();
        }
    }

    @Test
    public void createFilterTest() throws ServletException {
        if(jerryServletContext.createFilter(Mockito.mock(Filter.class).getClass()) == null){
            Assert.fail();
        }
    }
}
