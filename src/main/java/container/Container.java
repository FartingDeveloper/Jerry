package container;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Container {

    @Autowired
    @Qualifier("rootPath")
    private String rootPath;

    @Autowired
    private ServerSocket serverSocket;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private List<ServletContext> contexts;

    public void init(){

    }


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
