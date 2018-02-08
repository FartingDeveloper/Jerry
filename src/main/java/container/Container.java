package container;

import org.apache.http.impl.bootstrap.HttpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Container {

//    @Autowired
//    @Qualifier("rootPath")
//    private String rootPath;
//
//    @Autowired
//    private List<ServletContext> contexts;
//
//    @Autowired
//    private ExecutorService executorService;
//
//    @Autowired
//    private ServerSocket serverSocket;

    @Autowired
    private HttpServer server;

    public void run() throws IOException, InterruptedException {
        server.start();
        server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }
}
