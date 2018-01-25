package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.*;

@Configuration
@PropertySource("classpath:/com/rg/pool.properties")
public class ThreadPoolConfig {

    @Value("${queue.capacity}")
    private int capacity;

    @Value("${threadPool.corePoolSize}")
    private int corePoolSize;

    @Value("${threadPool.maximumPoolSize}")
    private int maximumPoolSize;

    @Value("${queue.keepAliveTime}")
    private long keepAliveTime;

    @Bean
    public TimeUnit unit(){
        return TimeUnit.MINUTES;
    }

    @Bean
    public BlockingQueue<Runnable> queue(){
        return new ArrayBlockingQueue<Runnable>(capacity);
    }

    @Bean
    public ExecutorService threadPool(TimeUnit unit, BlockingQueue<Runnable> queue){
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue);
    }

}
