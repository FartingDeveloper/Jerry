package servlet;

import loader.ContextLoader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import servlet.context.JerryServletContext;

import java.net.URISyntaxException;
import java.util.Map;

public class ContextLoaderTest {

    public static Map<String, JerryServletContext> contexts;
    public static JerryServletContext jerryServletContext;

    @BeforeClass
    public static void initClass() throws URISyntaxException {
        System.out.println(ContextLoader.class
                .getClassLoader().getResource("webapps").getPath());


        String path = ContextLoader.class
                .getClassLoader().getResource("webapps").toURI().getPath();
        System.out.println(path);


        contexts = new ContextLoader().load(path);

        jerryServletContext = contexts.values().iterator().next();
    }

    @Test
    public void test(){
        if(contexts == null){
            Assert.fail();
        }
    }

}
