package com.rg.loader.xml;

import com.rg.loader.ContextLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.rg.servlet.context.JerryServletContext;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class XmlContextLoader implements ContextLoader {

    private static final Logger LOG = LogManager.getLogger(XmlContextLoader.class);

    private static final String WEB_INF = "WEB-INF";
    private static final String WEB_XML = "web.xml";
    private static final String LIB = "lib";
    private static final String CLASSES = "classes";
    private static final String WEBAPPS = "webapps";

    private XmlParser parser;

    public XmlContextLoader() {
        parser = new XmlParser();
    }

    public Map<String, JerryServletContext> load(String path) {
        LOG.debug("Loading contexts.");
        Map<String, JerryServletContext> contexts = new HashMap<>();

        unzipWars(path + File.separator + WEBAPPS);

        File webapps = new File(path + File.separator + WEBAPPS);

        for (File file : webapps.listFiles()) {
            if (!isWar(file)) {
                JerryServletContext context = createContext(file, contexts);
                context.setContextName(file.getName());
                contexts.put(file.getName(), context);
            }
        }

        LOG.debug("Contexts loaded.");

        return contexts;
    }

    private JerryServletContext createContext(File file, Map<String, JerryServletContext> contexts) {
        LOG.debug("Creating context:" + file.getName());

        LOG.debug("Loading classes.");
        File jars = new File(file.getPath() + File.separator + WEB_INF + File.separator + LIB);
        File classes = new File(file.getPath() + File.separator + WEB_INF + File.separator + CLASSES);
        ClassLoader classLoader = loadClasses(jars, classes);

        LOG.debug("Loading resources.");
        Set<String> resources = loadResources(file);

        LOG.debug("Parsing web.xml.");
        File webXml = new File(file.getPath() + File.separator + WEB_INF + File.separator + WEB_XML);
        return parser.parseWebXml(webXml, classLoader, resources, contexts);
    }

    private Set<String> loadResources(File folder) {
        Set<String> resources = new HashSet<>();
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                resources.addAll(loadResources(fileEntry));
            } else {
                String resource = fileEntry.toURI().toString();
                resources.add(resource);
            }
        }
        return resources;
    }

    private ClassLoader loadClasses(File jars, File classes) {
        URLClassLoader loader;

        File[] files = jars.listFiles();
        int size = files.length + 1;

        try {
            URL[] urls = new URL[files.length + 1];
            urls[size - 1] = classes.getAbsoluteFile().toURI().toURL();

            for (int i = 0; i < size - 1; i++) {
                urls[i] = files[i].getAbsoluteFile().toURI().toURL();
            }

            loader = new URLClassLoader(urls, this.getClass().getClassLoader());
        } catch (MalformedURLException e) {
            LOG.error("Failed to load jars and classes.");
            throw new IllegalStateException(e.getCause());
        }
        return loader;
    }

    private void unzipWars(String path) {
        LOG.debug("Unzipping WARs.");
        File webapps = new File(path);
        for (File app : webapps.listFiles()) {
            if (isWar(app)) {
                JarFile war = null;
                try {
                    war = new JarFile(app);
                    extractWar(war);
                } catch (IOException e) {
                    LOG.error("WAR unzipping error.", e);
                } finally {
                    try {
                        war.close();
                    } catch (IOException e) {
                        //Nothing to do
                    }
                }
            }
        }
    }

    private boolean isWar(File file) {
        String fileName = file.getName();
        String extension;
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (extension.equals("war")) {
                return true;
            }
        }
        return false;
    }

    private File extractWar(JarFile war) {
        String outputDirName = makeOutputDir(war.getName());
        File outputDir = new File(outputDirName);

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        Enumeration<JarEntry> entries = war.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            File file = new File(outputDirName + File.separator + entry.getName());
            if (entry.isDirectory()) {
                file.mkdir();
                continue;
            }

            try (InputStream in = war.getInputStream(entry); OutputStream out = new FileOutputStream(file)) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(in, 1024);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out, 1024);

                while (bufferedInputStream.available() > 0) {
                    bufferedOutputStream.write(bufferedInputStream.read());
                }

                bufferedOutputStream.flush();
            } catch (IOException e) {
                LOG.error("WAR extracting error.", e);
            }
        }

        return outputDir;
    }

    private String makeOutputDir(String warName) {
        int index = warName.indexOf(".war");
        return warName.substring(0, index);
    }
}
