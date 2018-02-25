package http;

public class RequestLine {

    private static final String SP = " ";
    private static final String CRLF = "\r\n";

    private String line;
    private String method;
    private String uri;
    private String protocolVersion;

    public RequestLine(String line){
        this.line = line;
        int index = line.indexOf(SP);
        method = line.substring(0, index);
        line = line.substring(index + 1, line.length());
        index = line.indexOf(SP);
        uri = line.substring(0, index);
        line = line.substring(index + 1, line.length());
        protocolVersion = line.substring(0, line.length());
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public String toString() {
        return line;
    }
}
