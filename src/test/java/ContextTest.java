import loader.ContextLoader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import servlet.JerryServletContext;
import servlet.JerryServletRegistration;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

public class ContextTest {

    public static JerryServletContext servletContext;

    @BeforeClass
    public static void beforeAll() throws IllegalAccessException, InvocationTargetException, InstantiationException, URISyntaxException, NoSuchMethodException, NoSuchFieldException {
        //we need an outer class object to use the inner object constructor
        //(the inner class object needs to know about its parent object)
        ContextLoader outerObject = new ContextLoader();
        Field field = outerObject.getClass().getDeclaredField("classLoader");
        field.setAccessible(true);
        field.set(outerObject, outerObject.getClass().getClassLoader());
        //let's get the inner class
        //(we know that the outer class has only one inner class, so we can use index 0)
        Class<?> innerClass = ContextLoader.class.getDeclaredClasses()[0];
        //or if we know name of inner class we can use
        //Class<?> innerClass = Class.forName("full.package.name.OuterClass$InnerClass")

        //since constructor so we could use it to pass instance of outer class and change
        //its accessibility. We can use this code to get default constructor of InnerClass
        //since we know that this is the only constructor here
        Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
        //we could also use
        //Constructor<?> constructor = innerClass.getDeclaredConstructor(OuterClass.class);

        //the default constructor of the private class has same visibility that class has
        //so it is also private, so to be able to use it we need to make it accessible
        constructor.setAccessible(true);

        //now we are ready to create inner class instance
        Object parser = constructor.newInstance(outerObject, null);

        Method method = parser.getClass().getMethod("parseWebXml", File.class);
        method.setAccessible(true);

        URL url = parser.getClass().getClassLoader().getResource("web.xml");
        File file = new File(url.toURI());
        servletContext = (JerryServletContext) method.invoke(parser, file);
    }

    @Test
    public void servletRegistrationTest(){
        Collection<? extends ServletRegistration> servletRegistrations = servletContext.getServletRegistrations().values();
        for (ServletRegistration servletRegistration : servletRegistrations){
            Collection<String> mappings = servletRegistration.getMappings();
            System.out.println("Servlet name: " + servletRegistration.getName());
            System.out.println("Servlet class: " + servletRegistration.getClass());
            System.out.print("Mapping: ");
            for (String str : mappings){
                System.out.print(str + "; ");
            }
            System.out.println();
            System.out.println("Init params: ");
            Map<String, String> init = servletRegistration.getInitParameters();
            for (String key : init.keySet()){
                System.out.println(key + " - " + init.get(key));
            }
            System.out.println();
        }
    }

    @Test
    public void filterRegistrationTest(){
        Collection<? extends FilterRegistration> filterRegistrations = servletContext.getFilterRegistrations().values();
        for (FilterRegistration filterRegistration : filterRegistrations){
            System.out.println("Filter name: " + filterRegistration.getName());
            System.out.println("Filter class: " + filterRegistration.getClass());
            Collection<String> mappings = filterRegistration.getUrlPatternMappings();
            System.out.print("Mapping: ");
            for (String str : mappings){
                System.out.print(str + "; ");
            }
            System.out.println();
            Collection<String> servletMapping = filterRegistration.getServletNameMappings();
            System.out.print("ServletNameMapping: ");
            for (String str : servletMapping){
                System.out.print(str + "; ");
            }
            System.out.println();
            System.out.println("Init params: ");
            Map<String, String> init = filterRegistration.getInitParameters();
            for (String key : init.keySet()){
                System.out.println(key + " - " + init.get(key));
            }
            System.out.println();
        }
    }

    @Test
    public void initParamsTest(){
        Enumeration<String> names = servletContext.getInitParameterNames();
        while (names.hasMoreElements()){
            String name = names.nextElement();
            System.out.println("Init param name: " + name + " Init param value: " + servletContext.getInitParameter(name));
        }
    }

    @Test
    public void attributeTest(){
        servletContext.setAttribute("A", "B");
        if(servletContext.getAttribute("A") != "B"){
            Assert.fail();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void setInitParamTest(){
        servletContext.setInitParameter("A", "A");
    }

    @Test(expected = RuntimeException.class)
    public void requestDispatcherTest(){
        servletContext.getRequestDispatcher("/login");
    }

}
