package http;

import http.io.ByteOutputStream;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse extends HttpMessage{

    private static final String[][] HttpReplies = {{"100", "Continue"},
            {"101", "Switching Protocols"},
            {"200", "OK"},
            {"201", "Created"},
            {"202", "Accepted"},
            {"203", "Non-Authoritative Information"},
            {"204", "No Content"},
            {"205", "Reset Content"},
            {"206", "Partial Content"},
            {"300", "Multiple Choices"},
            {"301", "Moved Permanently"},
            {"302", "Found"},
            {"303", "See Other"},
            {"304", "Not Modified"},
            {"305", "Use Proxy"},
            {"306", "(Unused)"},
            {"307", "Temporary Redirect"},
            {"400", "Bad Request"},
            {"401", "Unauthorized"},
            {"402", "Payment Required"},
            {"403", "Forbidden"},
            {"404", "Not Found"},
            {"405", "Method Not Allowed"},
            {"406", "Not Acceptable"},
            {"407", "Proxy Authentication Required"},
            {"408", "Request Timeout"},
            {"409", "Conflict"},
            {"410", "Gone"},
            {"411", "Length Required"},
            {"412", "Precondition Failed"},
            {"413", "Request Entity Too Large"},
            {"414", "Request-URI Too Long"},
            {"415", "Unsupported Media Type"},
            {"416", "Requested Range Not Satisfiable"},
            {"417", "Expectation Failed"},
            {"500", "Internal Server Error"},
            {"501", "Not Implemented"},
            {"502", "Bad Gateway"},
            {"503", "Service Unavailable"},
            {"504", "Gateway Timeout"},
            {"505", "HTTP Version Not Supported"}};

    private String status;
    private int statusCode;

    private OutputStream outputStream;
    private ByteOutputStream contentOutputStream;

    public HttpResponse(OutputStream outputStream){
        this(outputStream, new ArrayList<>());
    }

    public HttpResponse(OutputStream outputStream, List<Header> headers){
        super(headers);
        this.outputStream = outputStream;
        contentOutputStream = new ByteOutputStream();
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
        return contentOutputStream;
    }

    public void flush(){

    }
}
