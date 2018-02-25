package http;

import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpParserTest {

    @Test
    public void test() throws IOException, HttpParser.WrongRequestException {
        ServerSocket serverSocket = new ServerSocket(8080);
        Socket socket = serverSocket.accept();
        HttpRequest request = HttpParser.parse(socket);
        System.out.println(request.toString());
    }
}
