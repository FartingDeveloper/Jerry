package com.rg.http;

import com.rg.http.core.Header;
import com.rg.http.io.RequestLine;
import com.rg.http.io.HttpRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequestTest extends HttpMessageTest{

    private static final String SP = " ";
    private static final String CRLF = "\r\n";

    public static final String METHOD = "GET";
    public static final String URI = "https://habrahabr.ru/post/215117/";
    public static final String VERSION = "HTTP/1.1";
    public static final String[] HEADER_NAMES = {"Accept", "Accept-Charset, Accept-Language, Host"};
    public static final String[] HEADER_VALUES = {"text/plain", "utf-8", "en-US", "en.wikipedia.org"};
    public static final String[][] PARAMS = {{"a","b"}, {"d","c"}};
    public static RequestLine requestLine;
    public static HttpRequest request;

    public static String tmpUri;

    @Before
    public void initObj(){
        StringBuilder builder = new StringBuilder(URI + "?");
        for(int i = 0; i < PARAMS.length; i++){
            builder.append(PARAMS[i][0] + "=" + PARAMS[i][1] + "&");
        }

        builder.deleteCharAt(builder.length()-1);
        tmpUri = builder.toString();

        requestLine = new RequestLine(METHOD + SP + tmpUri + SP + VERSION);
        List<Header> headerList = createList();
        request = new HttpRequest(requestLine, headerList);
        System.out.println(request);
    }

    private static List<Header> createList(){
        List<Header> headerList = new ArrayList<>();
        for (int i = 0; i < HEADER_NAMES.length; i++){
            headerList.add(new Header(HEADER_NAMES[i], HEADER_VALUES[i]));
        }
        return headerList;
    }

    @Test
    public void getRequestLineTest(){
        RequestLine line = request.getRequestLine();

        if(! line.getMethod().equals(METHOD)){
            Assert.fail();
        }

        if(! line.getProtocolVersion().equals(VERSION)){
            Assert.fail();
        }

        if(! line.getUri().equals(tmpUri)){
            Assert.fail();
        }
    }

    @Test
    public void getParamsTest(){
        Map<String, String[]> map = request.getRequestParameters();
        for(int i = 0; i < PARAMS.length; i++){
            if(! map.get(PARAMS[i][0])[0].equals(PARAMS[i][1])){
                Assert.fail();
            }
        }
    }

}
