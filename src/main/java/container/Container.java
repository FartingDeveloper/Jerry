package container;

import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.ServletContext;
import java.net.ServerSocket;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class Container {

    @Autowired
    private ServerSocket serverSocket;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private List<ServletContext> contexts;

}
