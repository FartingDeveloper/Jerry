package com.rg.servlet;

import com.sun.org.apache.bcel.internal.util.ClassLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import com.rg.servlet.registration.JerryRegistration;

import javax.servlet.*;
import java.util.HashMap;
import java.util.Map;

public abstract class RegistrationTest {

    public static final String name = "servlet";
    public static final String className = "ServletHandler";
    
    public JerryRegistration registration;

    @Before
    public abstract void init();

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
    public void initAgainTest() throws ClassNotFoundException, InstantiationException, ServletException, IllegalAccessException {
        ServletContext servletContext = Mockito.mock(ServletContext.class);
        ClassLoader loader = Mockito.mock(ClassLoader.class);
        Class clazz;

        if(registration instanceof ServletRegistration){
            clazz = Mockito.mock(Servlet.class).getClass();
        } else{
            clazz = Mockito.mock(Filter.class).getClass();
        }

        Mockito.when(servletContext.getClassLoader()).thenReturn(loader);
        Mockito.when(loader.loadClass(registration.getClassName())).thenReturn(clazz);

        registration.init(servletContext);

        String name = "Homer";
        String value = "Bart";
        registration.setInitParameter(name, value);
    }
    
}

