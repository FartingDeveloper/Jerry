import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import servlet.registration.JerryRegistration;

import javax.servlet.Registration;
import java.util.HashMap;
import java.util.Map;

public class RegistrationTest {

    public static final String name = "servlet";
    public static final String className = "ServletHandler";
    
    public Registration registration;

    @Before
    public void init(){
        registration = new JerryRegistration(name, className);
    }

    @Test
    public void nameTest(){
        if (! registration.getName().equals(name)){
            Assert.fail();
        }
    }

    @Test
    public void classNameTest(){
        if (! registration.getClassName().equals(className)){
            Assert.fail();
        }
    }

    @Test
    public void setInitParamTest(){
        String name = "Homer";
        String value = "Bart";
        registration.setInitParameter(name, value);

        if (! registration.getInitParameter(name).equals(value)){
            Assert.fail();
        }
    }

    @Test
    public void setInitParamsTest(){
        Map<String, String> map = new HashMap<>();
        map.put("Homer", "Bart");
        map.put("Marge", "Lisa");

        registration.setInitParameters(map);
        Map<String, String> initParams = registration.getInitParameters();

        for(String name : map.keySet()){
            if(! map.get(name).equals(initParams.get(name))){
                Assert.fail();
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNullInitParamNameTest(){
        String name = null;
        String value = "Bart";
        registration.setInitParameter(name, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNullInitParamValueTest(){
        String name = "Homer";
        String value = null;
        registration.setInitParameter(name, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInitParamsWithNullTest(){
        Map<String, String> map = new HashMap<>();
        map.put("Marge", "Meggy");
        map.put(null, "Bart");
        map.put("Marge", null);

        registration.setInitParameters(map);
    }

    @Test(expected = IllegalStateException.class)
    public void initAgainTest(){
        JerryRegistration jerryregistration = (JerryRegistration) registration;
        jerryregistration.setInitialized(true);

        String name = "Homer";
        String value = "Bart";
        registration.setInitParameter(name, value);
    }
    
}

