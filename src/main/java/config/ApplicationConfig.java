package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ThreadPoolConfig.class, SocketConfig.class})
public class ApplicationConfig {

}
