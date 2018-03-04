package http;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpMessageTest {

    public static final String[] HEADER_NAMES = {"Accept", "Accept-Charset, Accept-Language, Host"};
    public static final String[] HEADER_VALUES = {"text/plain", "utf-8", "en-US", "en.wikipedia.org"};
    public static HttpMessage message;

    @BeforeClass
    public static void initClass() throws IOException {
        List<Header> headerList = createList();
        message = new HttpMessage(headerList);
    }

    private static List<Header> createList(){
        List<Header> headerList = new ArrayList<>();
        for (int i = 0; i < HEADER_NAMES.length; i++){
            headerList.add(new Header(HEADER_NAMES[i], HEADER_VALUES[i]));
        }
        return headerList;
    }

    @Test
    public void containsHeaderTest(){
        for (int i = 0; i < HEADER_NAMES.length; i++){
            if(! message.containsHeader(HEADER_NAMES[i])){
                Assert.fail();
            }
        }
    }

    @Test
    public void getHeaderTest(){
        for (int i = 0; i < HEADER_NAMES.length; i++){
            if(message.getHeader(HEADER_NAMES[i]) == null){
                Assert.fail();
            }
        }
    }

    @Test
    public void addHeaderTest(){
        String headerName = "FART";
        String headerValue = "BOOBS";

        Header header = new Header(headerName, headerValue);

        message.addHeader(header);
        if(message.getHeader(headerName) == null){
            Assert.fail();
        }

    }

    @Test
    public void setHeaderTest(){
        String headerName = "FART";
        String headerValue = "BOOBS";
        String headerNewValue = "LOL";
        Header header = new Header(headerName, headerValue);

        message.addHeader(header);
        message.setHeader(headerName, headerNewValue);

        if(! message.getHeader(headerName).getValue().equals(headerNewValue)){
            Assert.fail();
        }
    }

    @Test
    public void removeHeaderTest(){
        String headerName = "FART";
        String headerValue = "BOOBS";

        Header header = new Header(headerName, headerValue);

        message.addHeader(header);
        message.removeHeader(header);

        if(message.getHeader(headerName) != null){
            Assert.fail();
        }
    }
}
