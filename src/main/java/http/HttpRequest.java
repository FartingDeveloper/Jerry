package http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest extends HttpMessage{

    private RequestLine requestLine;
    private Map<String, String[]> requestParams = new HashMap<>();

    public HttpRequest(RequestLine line, List<Header> headers){
        super(headers);
        this.requestLine = line;

        String uri = requestLine.getUri();
        int paramsIndex = uri.indexOf(Syntax.PARAMS_START);

        if(paramsIndex != -1){
            collectRequestParams(uri.substring(paramsIndex + 1, uri.length()));
        }
    }

    public HttpRequest(RequestLine line, List<Header> headers, String content){
        super(headers, content);
        this.requestLine = line;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public InputStream getContentInputStream(){
        if(content == null){
            return new ByteArrayInputStream(new byte[0]);
        }
        return new ByteArrayInputStream(getContent().getBytes());
    }

    public String getRequestParameterByName(String name){
        if(requestParams.get(name) != null){
            return requestParams.get(name)[0];
        }
        return null;
    }

    public Map<String, String[]> getRequestParameters(){
        return requestParams;
    }

    private void collectRequestParams(String line){
        int index = line.indexOf(Syntax.PARAMS_SEPARATOR);
        if(index != -1){
            collectRequestParams(line.substring(index + 1, line.length()));
            line = line.substring(0, index);
        }

        int equalityIndex = line.indexOf(Syntax.EQUALITY);
        String name = line.substring(0, equalityIndex);
        String value = line.substring(equalityIndex + 1, line.length());

        if(requestParams.containsKey(name)){
            String[] arr = requestParams.get(name);
            String[] newArr = Arrays.copyOf(arr, arr.length + 1);
            newArr[newArr.length - 1] = name;

            requestParams.put(name, newArr);
        } else{
            requestParams.put(name, new String[]{value});
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(requestLine.toString() + "\r\n");
        for (Header header : headers){
            builder.append(header.toString() + "\r\n");
        }
        return builder.toString();
    }
}
