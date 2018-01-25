package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.net.ServerSocket;

@Configuration
@PropertySource("classpath:/com/rg/socket.properties")
public class SocketConfig {

    @Value("${port}")
    private int port;

    @Bean(destroyMethod = "close")
    public ServerSocket serverSocket() throws IOException {
        return new ServerSocket(port);
    }

}
