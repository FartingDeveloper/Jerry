package com.rg.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.util.concurrent.*;

@Configuration
@PropertySource("server.properties")
public class ThreadPoolConfig {

    @Value("${asyncContextThreadPool.asyncContextCorePoolSize}")
    private int asyncContextCorePoolSize;

    @Value("${threadPool.capacity}")
    private int capacity;

    @Value("${threadPool.corePoolSize}")
    private int corePoolSize;

    @Value("${threadPool.maximumPoolSize}")
    private int maximumPoolSize;

    @Value("${queue.keepAliveTime}")
    private long keepAliveTime;

    @Bean(name = "unit")
    public TimeUnit unit(){
        return TimeUnit.MINUTES;
    }

    @Bean(name = "queue")
    public BlockingQueue<Runnable> queue(){
        return new ArrayBlockingQueue<Runnable>(capacity);
    }

    @Bean(name = "threadPool")
    public ExecutorService threadPool(@Qualifier("unit") TimeUnit unit, @Qualifier("queue") BlockingQueue<Runnable> queue) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue);
    }

    @Bean(name = "asyncContextThreadPool")
    public ScheduledExecutorService asyncContextThreadPool(){
        return Executors.newScheduledThreadPool(asyncContextCorePoolSize);
    }

}
