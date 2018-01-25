package container;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class RequestHandler implements Runnable {

    private Socket socket;
    private InetAddress ip;

    private InputStream in;
    private OutputStream out;

    public RequestHandler(Socket socket) throws IOException {
        this.socket = socket;
        ip = socket.getInetAddress();
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    public void run() {

    }

}
