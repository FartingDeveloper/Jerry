package com.rg.http;

import com.rg.http.core.Header;
import com.rg.http.core.HeaderElement;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class HeaderTest {

    public static final String HEADER_NAME = "Simpsons";
    public static final String[] ELEMENT_NAMES = {"Homer", "Marge"};
    public static final String[] ELEMENT_VALUES = {"Bart", "Lisa"};
    public static final String[][] PARAMS = {{"Eat", "Shorts"}, {"Play", "Jazz"}};
    public static String headerValue;
    public static Header header;

    @BeforeClass
    public static void init(){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < ELEMENT_NAMES.length; i++){
            builder.append(ELEMENT_NAMES[i]);
            builder.append("=");
            builder.append(ELEMENT_VALUES[i]);
            builder.append(";");
            for(int j = 0; j < PARAMS[i].length - 1; j++){
                builder.append(PARAMS[i][j]);
                builder.append("=");
                builder.append(PARAMS[i][j+1]);
            }
            builder.append(",");
        }
        builder.deleteCharAt(builder.length()-1);
        
        headerValue = builder.toString();
        System.out.println(headerValue);
        
        header = new Header(HEADER_NAME, headerValue);
    }

    @Test
    public void getNameTest(){
        if (! header.getName().equals(HEADER_NAME)){
            Assert.fail();
        }
    }

    @Test
    public void getValueTest(){
        if (! header.getValue().equals(headerValue)){
            Assert.fail();
        }
    }

    @Test
    public void getElementsTest(){
        int lenght = ELEMENT_NAMES.length - 1;
        List<HeaderElement> elements = header.getElements();
        for (int i = 0; i < elements.size(); i++){
            HeaderElement element = elements.get(i);
            if(! element.getName().equals(ELEMENT_NAMES[lenght - i])){
                Assert.fail();
            }
            if(! element.getValue().equals(ELEMENT_VALUES[lenght - i])){
                Assert.fail();
            }

            for(int j = 0; j < PARAMS[lenght - i].length - 1; j++){
                if(! element.getParameterByName(PARAMS[lenght - i][j]).equals(PARAMS[lenght - i][j+1])){
                    Assert.fail();
                }
            }
        }
    }

}
