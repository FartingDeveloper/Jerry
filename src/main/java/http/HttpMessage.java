package http;

import java.util.ArrayList;
import java.util.List;

public class HttpMessage {

    protected List<Header> headers;
    protected String content;

    public HttpMessage(List<Header> headers){
        this.headers = headers;
    }

    public HttpMessage(List<Header> headers, String content){
        this.headers = headers;
        this.content = content;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public boolean containsHeader(String name){
        for (Header hdr : headers){
            if(hdr.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public boolean containsHeader(Header header){
        return containsHeader(header.getName());
    }

    public Header getHeader(String name){
        Header header = null;
        for (Header hdr : headers){
            if(hdr.getName().equals(name)){
                header = hdr;
            }
        }

        return header;
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
        addHeader(header);
    }

    public void removeHeader(String name){
        for(int i = 0; i < headers.size(); i++){
            if(headers.get(i).getName().equals(name)){
                headers.remove(i);
            }
        }
    }

    public void removeHeader(Header header){
        removeHeader(header.getName());
    }

    public String getContent(){
        return content;
    }
}
