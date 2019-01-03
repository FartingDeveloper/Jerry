package com.rg;

import com.rg.http.HttpRequestHandler;
import com.rg.http.HttpServer;
import com.rg.http.RequestHandler;
import com.rg.http.core.HTTP;
import com.rg.loader.ContextLoader;
import com.rg.loader.xml.XmlContextLoader;
import com.rg.servlet.context.JerryServletContext;
import com.rg.servlet.registration.JerryFilterRegistration;
import com.rg.servlet.registration.JerryServletRegistration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.util.*;

public class Jerry {

    private static final Logger LOG = LogManager.getLogger(Jerry.class);

    private HttpServer server;

    public Jerry(Properties properties) throws IOException {
        String rootPath = getRootPath();
        ContextLoader contextLoader = new XmlContextLoader();
        Map<String, JerryServletContext> contexts = contextLoader.load(rootPath);

        this.server = new HttpServer(properties, contexts);
    }

    public void start() {
        server.run();
    }

    private static String getRootPath() {
        File jar = null;
        try {
            jar = new File(Jerry.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            LOG.debug("Root path: " + jar.getAbsolutePath());
        } catch (URISyntaxException e) {
            LOG.error("Root path can't be found.");
            throw new RuntimeException("Root path can't be found.");
        }
        return jar.getParent();
    }

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(Jerry.class.getResourceAsStream("/server.properties"));

        Jerry jerry = new Jerry(properties);
        jerry.start();
    }

}
