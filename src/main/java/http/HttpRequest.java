package http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class HttpRequest extends HttpMessage{

    private RequestLine requestLine;

    public HttpRequest(RequestLine line, List<Header> headers){
        super(headers);
        this.requestLine = line;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(requestLine.toString() + "\r\n");
        for (Header header : headers){
            builder.append(header.toString() + "\r\n");
        }
        return builder.toString();
    }
}
