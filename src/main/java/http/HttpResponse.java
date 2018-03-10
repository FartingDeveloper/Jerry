package http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse extends HttpMessage{

    private String protocolVersion;

    private String status;
    private int statusCode = -1;

    private OutputStream outputStream;
    private ByteArrayOutputStream contentOutputStream;

    private boolean commited;

    public HttpResponse(OutputStream outputStream, RequestLine line){
        this(outputStream, line.getProtocolVersion(), new ArrayList<>());
    }

    public HttpResponse(OutputStream outputStream, String protocolVersion, List<Header> headers){
        super(headers);
        this.outputStream = outputStream;
        this.protocolVersion = protocolVersion;
        contentOutputStream = new ByteArrayOutputStream();
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
        this.statusCode = code;
        this.status = status;
    }

    public OutputStream getContentOutputStream(){
        return contentOutputStream;
    }

    public void flush() throws IOException {
        if(! commited){
            commited = true;

            String responseLine = protocolVersion + Syntax.SP + statusCode + Syntax.SP + status + Syntax.CRLF;
            outputStream.write(responseLine.getBytes());

            for (Header header : headers){
                String result = header + Syntax.CRLF;
                outputStream.write(result.getBytes());
            }

            outputStream.write(Syntax.CRLF.getBytes());
            outputStream.write(contentOutputStream.toByteArray());
            outputStream.flush();

            contentOutputStream.reset();
        }

        outputStream.write(contentOutputStream.toByteArray());
        outputStream.flush();

        contentOutputStream.reset();
    }

    public boolean isCommited() {
        return commited;
    }

    public void commit(){
        commited = true;
    }
}
