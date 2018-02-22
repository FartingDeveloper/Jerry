package http;

import http.io.ByteOutputStream;
import http.io.StringOutputStream;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HttpResponse extends HttpMessage{

    private String status;
    private int statusCode;

    private ByteOutputStream outputStream;

    public HttpResponse(List<Header> headers){
        super(headers);
        outputStream = new ByteOutputStream();
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

    public OutputStream getContentOutputStream(){
        return outputStream;
    }
}
