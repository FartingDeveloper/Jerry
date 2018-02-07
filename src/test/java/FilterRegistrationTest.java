import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import servlet.JerryFilterRegistration;

import javax.servlet.DispatcherType;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class FilterRegistrationTest extends RegistrationTest {

    public JerryFilterRegistration jerryFilterRegistration;

    @Before
    public void init(){
        super.init();
        jerryFilterRegistration = new JerryFilterRegistration(name, className);
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
    public void addMappingForUrlsToInitializedFilterTest(){
        jerryFilterRegistration.setInitialized(true);
        
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
    public void addMappingForServletNamesToInitializedFilterTest(){
        jerryFilterRegistration.setInitialized(true);

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
