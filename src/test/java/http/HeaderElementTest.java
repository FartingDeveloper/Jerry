package http;

import http.HeaderElement;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

public class HeaderElementTest {

    public static final String ELEMENT_NAME = "Homer";
    public static final String ELEMENT_VALUE = "Marge";
    public static final String[][] PARAMS = {{"Bart", "EatMyShorts"}, {"Lisa", "LikeJazz"}};
    public static HeaderElement element;

    @BeforeClass
    public static void init(){
        StringBuilder builder = new StringBuilder();
        builder.append(ELEMENT_NAME);
        builder.append("=");
        builder.append(ELEMENT_VALUE);
        builder.append(";");
        for(int i = 0; i < PARAMS.length; i++){
            for(int j = 0; j < PARAMS[i].length - 1; j++){
                builder.append(PARAMS[i][j]);
                builder.append("=");
                builder.append(PARAMS[i][j+1]);
            }
            builder.append(";");
        }

        builder.deleteCharAt(builder.length()-1);

        System.out.println(builder.toString());

        element = new HeaderElement(builder.toString());
    }

    @Test
    public void getNameTest(){
        if(! element.getName().equals(ELEMENT_NAME)){
            Assert.fail();
        }
    }

    @Test
    public void getValueTest(){
        if(! element.getValue().equals(ELEMENT_VALUE)){
            Assert.fail();
        }
    }

    @Test
    public void getParamsTest(){
        Map<String, String> map = element.getParameters();

        for(int i = 0; i < PARAMS.length; i++){
            for(int j = 0; j < PARAMS[i].length - 1; j++){
                if(!map.get(PARAMS[i][j]).equals(PARAMS[i][j+1])){
                    Assert.fail();
                }
            }
        }
    }

    @Test
    public void getParamsByNameTest(){
        for(int i = 0; i < PARAMS.length; i++){
            for(int j = 0; j < PARAMS[i].length - 1; j++){
                if(!element.getParameterByName(PARAMS[i][j]).equals(PARAMS[i][j+1])){
                    Assert.fail();
                }
            }
        }
    }

}
