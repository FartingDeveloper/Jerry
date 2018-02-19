package http;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HttpResponse {

    private RequestLine requestLine;

    private String status;
    private int statusCode;

    private List<Header> headers;

    public HttpResponse(RequestLine line){
        this.requestLine = line;
        headers = new ArrayList<>();
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public boolean containsHeader(String name){
        return containsHeader(new Header(name, null));
    }

    public boolean containsHeader(Header header){
        for (Header hdr : headers){
            if(hdr.getName().equals(header.getName())){
                return true;
            }
        }
        return false;
    }

    public Header[] getFirstHeader(String name){
        return getFirstHeader(new Header(name, null));
    }

    public Header[] getFirstHeader(Header header){
        List<Header> tmp = new LinkedList<>();
        for (Header hdr : headers){
            if(hdr.getName().equals(header.getName())){
                tmp.add(hdr);
            }
        }

        return tmp.toArray(new Header[tmp.size()]);
    }

    public void addHeader(String name, String value){
        addHeader(new Header(name, value));
    }

    public void addHeader(Header header){
        headers.add(header);
    }

    public void setHeader(String name, String value){
        setHeader(new Header(name, value));
    }

    public void setHeader(Header header){
        for(int i = 0; i < headers.size(); i++){
            if(headers.get(i).getName().equals(header.getName())){
                headers.set(i, header);
                return;
            }
        }
    }

    public void removeHeader(String name){
        removeHeader(new Header(name, null));
    }

    public void removeHeader(Header header){
        for(int i = 0; i < headers.size(); i++){
            if(headers.get(i).getName().equals(header.getName())){
                headers.remove(i);
            }
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
