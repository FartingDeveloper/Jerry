package http;

import http.io.ByteOutputStream;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse extends HttpMessage{

    private String protocolVersion;

    private String status;
    private int statusCode;

    private OutputStream outputStream;
    private ByteOutputStream contentOutputStream;

    public HttpResponse(OutputStream outputStream, String protocolVersion){
        this(outputStream, protocolVersion, new ArrayList<>());
    }

    public HttpResponse(OutputStream outputStream, String protocolVersion, List<Header> headers){
        super(headers);
        this.outputStream = outputStream;
        this.protocolVersion = protocolVersion;
        contentOutputStream = new ByteOutputStream();
    }

    public String getStatus() {
        return status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatus(int code, String status){
        this.status = code + " " + status;
    }

    public OutputStream getContentOutputStream(){
        return contentOutputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
