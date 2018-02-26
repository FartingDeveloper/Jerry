package servlet;

import http.HttpParser;
import http.HttpRequest;
import http.HttpResponse;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServletResponseTest {

    public static ServerSocket serverSocket;

    @BeforeClass
    public static void init() throws IOException {
        serverSocket = new ServerSocket(8080);
    }

    @Test
    public void sendResponseTest() throws IOException, HttpParser.WrongRequestException, InterruptedException {
            Socket socket = serverSocket.accept();
            HttpRequest request = HttpParser.parse(socket);
            HttpResponse response = new HttpResponse(socket.getOutputStream(), request.getRequestLine());
            response.setStatus(200, "OK");
            response.setHeader("Content-Type", "text/html");
            String res = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "  <meta charset=\"utf-8\">\n" +
                    "  <meta name=\"viewport\" content=\"width=device-width\">\n" +
                    "  <title>JS Bin</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "HELLO\n";
            response.getContentOutputStream().write(res.getBytes());
            Thread.sleep(5000);
            res =                     "</body>\n" +
                    "</html>";
            response.getContentOutputStream().write(res.getBytes());
            response.flush();
            socket.close();
    }

}
