package container;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class Container {

    @Autowired
    private ServerSocket serverSocket;

    @Autowired
    private ExecutorService executorService;

    public void run() {
        for(;;){
            try {
                Socket socket = serverSocket.accept();
                Runnable requestHandler = new RequestHandler(socket);
                executorService.execute(requestHandler);
            } catch (IOException e) {
                e.printStackTrace(); //LOG4G
            }
        }
    }

}
