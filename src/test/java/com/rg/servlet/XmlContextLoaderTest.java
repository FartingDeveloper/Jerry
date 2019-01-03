package com.rg.servlet;

import com.rg.loader.xml.XmlContextLoader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.rg.servlet.context.JerryServletContext;

import javax.servlet.ServletException;
import java.net.URISyntaxException;
import java.util.Map;

public class XmlContextLoaderTest {

    public static Map<String, JerryServletContext> contexts;

    @BeforeClass
    public static void initClass() throws URISyntaxException, ClassNotFoundException, InstantiationException, ServletException, IllegalAccessException {
        System.out.println(XmlContextLoader.class
                .getClassLoader().getResource("webapps").getPath());


        String path = XmlContextLoader.class
                .getClassLoader().getResource("webapps").toURI().getPath();
        System.out.println(path);

        contexts = new XmlContextLoader().load("target/test-classes");
    }

    @Test
    public void test(){
        if(contexts == null){
            Assert.fail();
        }
    }

}
