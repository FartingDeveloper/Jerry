package servlet;

import com.sun.org.apache.bcel.internal.util.ClassLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import servlet.registration.JerryFilterRegistration;

import javax.servlet.*;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class FilterRegistrationTest extends RegistrationTest {

    public JerryFilterRegistration jerryFilterRegistration;

    @Before
    public void init(){
        registration = new JerryFilterRegistration(name, className);
        jerryFilterRegistration = (JerryFilterRegistration) registration;
    }

    @Test
    public void addMappingForUrlsTest(){
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        String[] urls = {"/homer", "/bart"};

        jerryFilterRegistration.addMappingForUrlPatterns(dispatcherTypes, false, urls);
        if(! jerryFilterRegistration.getUrlPatternMappings().containsAll(Arrays.asList(urls))){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullMappingForUrlsTest(){
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        String[] urls = {"/homer", null};
        
        jerryFilterRegistration.addMappingForUrlPatterns(dispatcherTypes, false, urls);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addEmptyMappingForUrlsTest(){
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        String[] urls = {"/homer",""};

        jerryFilterRegistration.addMappingForUrlPatterns(dispatcherTypes, false, urls);
    }

    @Test(expected = IllegalStateException.class)
    public void addMappingForUrlsToInitializedFilterTest() throws ClassNotFoundException, InstantiationException, ServletException, IllegalAccessException {
        ServletContext servletContext = Mockito.mock(ServletContext.class);
        ClassLoader loader = Mockito.mock(ClassLoader.class);
        Filter filter = Mockito.mock(Filter.class);
        Class clazz = filter.getClass();

        Mockito.when(servletContext.getClassLoader()).thenReturn(loader);
        Mockito.when(loader.loadClass(jerryFilterRegistration.getClassName())).thenReturn(clazz);

        jerryFilterRegistration.init(servletContext);
        
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        String[] urls = {"/homer", "/bart"};

        jerryFilterRegistration.addMappingForUrlPatterns(dispatcherTypes, false, urls);
    }

    @Test
    public void addMappingForServletNamesTest(){
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        String[] servletNames = {"homer", "bart"};

        jerryFilterRegistration.addMappingForServletNames(dispatcherTypes, false, servletNames);
        if(! jerryFilterRegistration.getServletNameMappings().containsAll(Arrays.asList(servletNames))){
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullMappingForServletNamesTest(){
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        String[] servletNames = {"homer", null};

        jerryFilterRegistration.addMappingForServletNames(dispatcherTypes, false, servletNames);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addEmptyMappingForServletNamesTest(){
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        String[] servletNames = {"homer",""};

        jerryFilterRegistration.addMappingForServletNames(dispatcherTypes, false, servletNames);
    }

    @Test(expected = IllegalStateException.class)
    public void addMappingForServletNamesToInitializedFilterTest() throws ClassNotFoundException, InstantiationException, ServletException, IllegalAccessException {
        ServletContext servletContext = Mockito.mock(ServletContext.class);
        ClassLoader loader = Mockito.mock(ClassLoader.class);
        Filter filter = Mockito.mock(Filter.class);
        Class clazz = filter.getClass();

        Mockito.when(servletContext.getClassLoader()).thenReturn(loader);
        Mockito.when(loader.loadClass(jerryFilterRegistration.getClassName())).thenReturn(clazz);

        jerryFilterRegistration.init(servletContext);

        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        String[] servletNames = {"homer", "bart"};

        jerryFilterRegistration.addMappingForServletNames(dispatcherTypes, false, servletNames);
    }

    @Test
    public void getDispatcherTypesFromUrlTest(){
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        String[] urls = {"/homer", "/bart"};

        jerryFilterRegistration.addMappingForUrlPatterns(dispatcherTypes, false, urls);
        if(jerryFilterRegistration.getDispatcherTypes("/bart") == null){
            Assert.fail();
        }
    }

    @Test
    public void getDispatcherTypesFromServletNamesTest(){
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        String[] servletNames = {"homer", "bart"};

        jerryFilterRegistration.addMappingForServletNames(dispatcherTypes, false, servletNames);
        if(jerryFilterRegistration.getDispatcherTypes("bart") == null){
            Assert.fail();
        }
    }

    @Test
    public void getDispatcherTypes(){
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        String[] urls = {"/homer", "/bart"};

        jerryFilterRegistration.addMappingForUrlPatterns(dispatcherTypes, false, urls);

        Set<String> set = new HashSet<>();
        for(int i = 0; i < urls.length; i++){
            set.add(urls[i]);
        }

        if(jerryFilterRegistration.getDispatcherTypes(set) == null){
            Assert.fail();
        }
    }

}
