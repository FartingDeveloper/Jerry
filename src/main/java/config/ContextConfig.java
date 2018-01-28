package config;

import loader.ContextLoader;
import loader.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

@Configuration
public class ContextConfig {

    private Logger logger = LogManager.getLogger("config.ContextConfig");

    private static final String WEB_APPS = "webapps";

    @Bean
    public String rootPath(){
        File jar = null;
        try {
            jar = new File(ContextConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            logger.error("Root path can't be found.");
            throw new BeanCreationException("Root path can't be found.");
        }
        return jar.getParent();
    }

    @Bean
    public Loader<ServletContext> contextLoader(){
        return new ContextLoader();
    }

    @Bean
    public List<ServletContext> contexts(Loader<ServletContext> loader, String rootPath){
        return loader.load(rootPath + File.separator + WEB_APPS);
    }

}
