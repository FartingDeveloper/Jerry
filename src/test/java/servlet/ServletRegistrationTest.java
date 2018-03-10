package servlet;

import com.sun.org.apache.bcel.internal.util.ClassLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import servlet.registration.JerryServletRegistration;

import javax.servlet.*;
import java.util.Collection;

public class ServletRegistrationTest extends RegistrationTest{

    public JerryServletRegistration servletRegistration;

    @Before
    public void init(){
        registration = new JerryServletRegistration(name, className);
        servletRegistration = (JerryServletRegistration) registration;
    }

    @Test
    public void addMappingTest(){
        String[] strings = {"Bart", "Lisa"};
        servletRegistration.addMapping(strings);

        Collection<String> mapping= servletRegistration.getMappings();

        for (String name : strings){
            if (! mapping.contains(name)){
                Assert.fail();
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullMappingTest(){
        String[] strings = {"Bart", null};
        servletRegistration.addMapping(strings);
    }

    @Test(expected = IllegalStateException.class)
    public void addMappingToInitializedServletTest() throws ClassNotFoundException, InstantiationException, ServletException, IllegalAccessException {
        ServletContext servletContext = Mockito.mock(ServletContext.class);
        ClassLoader loader = Mockito.mock(ClassLoader.class);
        Servlet servlet = Mockito.mock(Servlet.class);
        Class clazz = servlet.getClass();

        Mockito.when(servletContext.getClassLoader()).thenReturn(loader);
        Mockito.when(loader.loadClass(servletRegistration.getClassName())).thenReturn(clazz);

        servletRegistration.init(servletContext);

        String[] strings = {"Bart", "Lisa"};
        servletRegistration.addMapping(strings);
    }

}
