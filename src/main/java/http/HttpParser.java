package http;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HttpParser {

    private static final String CHARSET = "ASCII";
    private static final String CRLF = "\r\n";

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

    public static HttpRequest parse(Socket socket) throws IOException, WrongRequestException {
        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, CHARSET));

        RequestLine requestLine = new RequestLine(reader.readLine());

        List<Header> headers = new ArrayList<>();

        String header;
        while ((header = reader.readLine()) != CRLF || header != null){
            header = header.trim();

            int index = header.indexOf(":");
            if(index == -1){
                throw new WrongRequestException();
            }

            String name = header.substring(0, index);
            String value = header.substring(index + 1, header.length());

            headers.add(new Header(name, value));
        }

        if(header == null){
            return new HttpRequest(requestLine, headers);
        }else{
            if((reader.readLine()) != CRLF){
                throw new WrongRequestException();
            }
        }

        StringBuilder content = new StringBuilder();
        String tmp;
        while ((tmp = reader.readLine()) != null){
            content.append(tmp);
        }

        return new HttpRequest(requestLine, headers, content.toString());
    }

    public static class WrongRequestException extends Exception{

    }

}
