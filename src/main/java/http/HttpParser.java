package http;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HttpParser {

    private static final String CHARSET = "ASCII";
    private static final String NAME_VALUE_SEPARATOR = ":";
    private static final String CONTENT_LENGTH = "Content-Length";

    public static HttpRequest parse(Socket socket) throws IOException, WrongRequestException {
        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, CHARSET));

        RequestLine requestLine = new RequestLine(reader.readLine());

        List<Header> headers = new ArrayList<>();

        int contentLength = -1;

        String header;
        while (! (header = reader.readLine()).isEmpty()){
            int index = header.indexOf(NAME_VALUE_SEPARATOR);
            if(index == -1){
                throw new WrongRequestException();
            }

            String name = header.substring(0, index);
            String value = header.substring(index + 1, header.length()).trim();

            headers.add(new Header(name, value));

            if(name.equals(CONTENT_LENGTH)){
                contentLength = Integer.valueOf(value);
            }
        }

        if(header.isEmpty() && contentLength == -1){
            return new HttpRequest(requestLine, headers);
        }

        StringBuilder content = new StringBuilder();
        int i = 0;
        while (i != contentLength){
            content.append(reader.readLine());
        }

        return new HttpRequest(requestLine, headers, content.toString());
    }

    public static class WrongRequestException extends Exception{

    }

}
