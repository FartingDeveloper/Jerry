package servlet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import servlet.registration.JerryServletRegistration;

import javax.servlet.ServletRegistration;
import java.util.Collection;

public class ServletRegistrationTest extends RegistrationTest{

    public ServletRegistration servletRegistration;

    @Before
    public void init(){
        super.init();
        servletRegistration = new JerryServletRegistration(name, className);
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
    public void addMappingToInitializedServletTest(){
        JerryServletRegistration jerryServletRegistration = (JerryServletRegistration) servletRegistration;
        jerryServletRegistration.setInitialized(true);

        String[] strings = {"Bart", "Lisa"};
        servletRegistration.addMapping(strings);
    }

}
