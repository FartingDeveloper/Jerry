package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.*;

@Configuration
@PropertySource("classpath:/com/rg/server.properties")
public class ThreadPoolConfig {

    @Value("${threadPool.corePoolSize}")
    private int corePoolSize;

    @Bean
    public ScheduledExecutorService threadPool(){
        return Executors.newScheduledThreadPool(corePoolSize);
    }

}
