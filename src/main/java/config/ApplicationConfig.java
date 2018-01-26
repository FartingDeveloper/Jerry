package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.logging.Logger;

@Configuration
@Import({ThreadPoolConfig.class, SocketConfig.class, ContextConfig.class})
public class ApplicationConfig {

    @Bean
    public Logger logger(){
       return null;
    }

}
