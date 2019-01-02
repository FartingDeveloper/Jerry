package com.rg.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.rg.servlet.registration.JerryFilterRegistration;
import com.rg.servlet.context.JerryServletContext;
import com.rg.servlet.registration.JerryServletRegistration;

import javax.servlet.DispatcherType;
import javax.servlet.HttpMethodConstraintElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class XmlContextContextLoader implements ContextLoader {

    private static final String WEB_INF = "WEB-INF";
    private static final String WEB_XML = "web.xml";
    private static final String LIB = "lib";
    private static final String CLASSES = "classes";
    private static final String WEBAPPS = "webapps";

    private Map<String, JerryServletContext> contexts = new HashMap<>();
    private ClassLoader classLoader;
    private Set<String> resources;

    private Logger LOG = LogManager.getLogger(XmlContextContextLoader.class);

    private XmlParser parser = new XmlParser();

    public Map<String, JerryServletContext> load(String path) {
        LOG.debug("Loading contexts.");

        LOG.debug("Unzipping WARs.");
        unzipWars(path + File.separator + WEBAPPS);

        File webapps = new File(path + File.separator + WEBAPPS);

        for (File file : webapps.listFiles()){
            if(! isWar(file)){
                JerryServletContext context = createContext(file);
                context.setContextName(file.getName());
                contexts.put(file.getName(), context);
            }
        }

        LOG.debug("Contexts loaded.");

        return contexts;
    }

    private JerryServletContext createContext(File file){
        LOG.debug("Creating context:" + file.getName());

        LOG.debug("Loading classes.");
        File jars = new File(file.getPath() + File.separator + WEB_INF + File.separator + LIB);
        File classes = new File(file.getPath() + File.separator + WEB_INF + File.separator + CLASSES);
        classLoader = loadClasses(jars, classes);

        LOG.debug("Loading resources.");
        resources = loadResources(file);

        LOG.debug("Parsing web.xml.");
        File webXml = new File(file.getPath() + File.separator + WEB_INF + File.separator + WEB_XML);
        return parser.parseWebXml(webXml);
    }

    private Set<String> loadResources(File folder){
        Set<String> res = new HashSet<>();
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
               res.addAll(loadResources(fileEntry));
            } else {
                String resource = fileEntry.toURI().toString();
                res.add(resource);
            }
        }
        return res;
    }

    private ClassLoader loadClasses(File jars, File classes) {
        URLClassLoader loader = null;

        File[] files = jars.listFiles();
        int size = files.length + 1;

        try {
            URL[] urls = new URL[files.length + 1];
            urls[size - 1] = classes.getAbsoluteFile().toURI().toURL();

            for(int i = 0; i < size - 1; i++){
                urls[i] = files[i].getAbsoluteFile().toURI().toURL();
            }

            loader = new URLClassLoader(urls, this.getClass().getClassLoader());
        } catch (MalformedURLException e) {
            LOG.error("Failed to load jars and classes.");
        }
        return loader;
    }

    private void unzipWars(String path){
        LOG.debug("Unzipping WARs.");
        File webapps = new File(path);
        for (File app : webapps.listFiles()){
            if(isWar(app)){
                JarFile war = null;
                try {
                    war = new JarFile(app);
                    extractWar(war);
                } catch (IOException e) {
                    LOG.error("WAR unzipping error.", e);
                }
                finally {
                    try {
                        war.close();
                    } catch (IOException e) {
                        //Nothing to do
                    }
                }
            }
        }
    }

    private boolean isWar(File file){
        String fileName = file.getName();
        String extension;
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            extension = fileName.substring(fileName.lastIndexOf(".")+1);
            if(extension.equals("war")){
                return true;
            }
        }
        return false;
    }

    private File extractWar(JarFile war){
        String outputDirName = makeOutputDir(war.getName());
        File outputDir = new File(outputDirName);

        if(! outputDir.exists()){
            outputDir.mkdirs();
        }

        Enumeration<JarEntry> entries = war.entries();

        while (entries.hasMoreElements()){
            JarEntry entry = entries.nextElement();
            File file = new File(outputDirName + File.separator + entry.getName());
            if(entry.isDirectory()){
                file.mkdir();
                continue;
            }

            try(InputStream in = war.getInputStream(entry); OutputStream out = new FileOutputStream(file)) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(in, 1024);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out, 1024);

                while (bufferedInputStream.available() > 0){
                    bufferedOutputStream.write(bufferedInputStream.read());
                }

                bufferedOutputStream.flush();
            } catch (IOException e) {
                LOG.error("WAR extracting error.", e);
            }
        }

        return outputDir;
    }

    private String makeOutputDir(String warName){
        int index = warName.indexOf(".war");
        return warName.substring(0, index);
    }

    private class XmlParser {

        private JerryServletContext servletContext;

        public JerryServletContext parseWebXml(File file){

            servletContext = new JerryServletContext(contexts, resources, classLoader);

            try {
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document web = documentBuilder.parse(file);

                Node root = web.getDocumentElement();
                NodeList childNodes = root.getChildNodes();

                for(int i = 0; i < childNodes.getLength(); i++){
                    Node node = childNodes.item(i);
                    switch (node.getNodeName()){
                        case "context-param": {
                            KeyValuePair pair = collectParams(node);
                            servletContext.setInitParameter(pair.getKey(), pair.getValue());
                            break;
                        }
                        case "servlet": {
                            collectServlets(node);
                            break;
                        }
                        case "servlet-mapping": {
                            collectServletMapping(node);
                            break;
                        }
                        case "filter": {
                            collectFilters(node);
                            break;
                        }
                        case "filter-mapping": {
                            collectFilterMapping(node);
                            break;
                        }
                        case "listener": {
                            collectListeners(node);
                            break;
                        }
                        case "security-constraint": {

                            break;
                        }
                    }
                }
            } catch (ParserConfigurationException e) {
                LOG.error("Parser error.", e);
            } catch (SAXException e) {
                LOG.error("Parser error.", e);
            } catch (IOException e) {
                LOG.error("IOException.", e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                LOG.error("Class not found exception.", e);
            }

            return servletContext;
        }

        private void collectListeners(Node node) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
            NodeList listener = node.getChildNodes();

            for (int i = 0; i < listener.getLength(); i++){
                Node listenerClass = listener.item(i);

                switch (listenerClass.getNodeName()){
                    case "listener-class": {
                        Class<? extends EventListener> clazz = (Class<? extends EventListener>) classLoader.loadClass(listenerClass.getTextContent());
                        servletContext.addListener(clazz);
                    }
                }
            }
        }

        private void collectFilters(Node obj){
            NodeList params = obj.getChildNodes();

            String filterName = null;
            String filterClass = null;
            Map<String, String> initParams = new HashMap<>();

            for (int j = 0; j < params.getLength(); j++){
                Node node = params.item(j);

                switch (node.getNodeName()){
                    case "filter-name": {
                        filterName = node.getTextContent();
                        break;
                    }
                    case "filter-class": {
                        filterClass = node.getTextContent();
                        break;
                    }
                    case "init-param": {
                        KeyValuePair pair = collectParams(node);
                        initParams.put(pair.getKey(), pair.getValue());
                        break;
                    }
                }
            }

            JerryFilterRegistration jerryFilterRegistration = new JerryFilterRegistration(filterName, filterClass);
            jerryFilterRegistration.setInitParameters(initParams);

            servletContext.addFilterRegistration(jerryFilterRegistration);
        }

        private void collectFilterMapping(Node mapping){
            NodeList urls = mapping.getChildNodes();

            String filterName = null;
            Set<String> filterPatterns = new HashSet<>();
            Set<String> servletNames = new HashSet<>();
            Set<DispatcherType> dispatcherTypeSet = new HashSet<>();

            for (int i = 0; i < urls.getLength(); i++){
                Node url = urls.item(i);
                switch (url.getNodeName()){
                    case "filter-name":
                        filterName = url.getTextContent();
                        break;
                    case "url-pattern":
                        filterPatterns.add(url.getTextContent());
                        break;
                    case "servlet-name": {
                        servletNames.add(url.getTextContent());
                        break;
                    }
                    case "dispatcher": {
                        dispatcherTypeSet.add(DispatcherType.valueOf(url.getTextContent()));
                        break;
                    }
                }
            }

            if(dispatcherTypeSet.isEmpty()){
                servletContext.getFilterRegistration(filterName).addMappingForUrlPatterns(null,
                        false, filterPatterns.toArray(new String[filterPatterns.size()]));

                servletContext.getFilterRegistration(filterName).addMappingForServletNames(null,
                        false, servletNames.toArray(new String[servletNames.size()]));
            } else{
                servletContext.getFilterRegistration(filterName).addMappingForUrlPatterns(EnumSet.copyOf(dispatcherTypeSet),
                        false, filterPatterns.toArray(new String[filterPatterns.size()]));

                servletContext.getFilterRegistration(filterName).addMappingForServletNames(EnumSet.copyOf(dispatcherTypeSet),
                        false, servletNames.toArray(new String[servletNames.size()]));
            }
    }

        private void collectServlets(Node obj){
            NodeList params = obj.getChildNodes();

            String servletName = null;
            String servletClass = null;
            Map<String, String> initParams = new HashMap<>();
            int loadOnStartup = -1;

            for (int j = 0; j < params.getLength(); j++){
                Node node = params.item(j);

                switch (node.getNodeName()){
                    case "servlet-name": {
                        servletName = node.getTextContent();
                        break;
                    }
                    case "servlet-class": {
                        servletClass = node.getTextContent();
                        break;
                    }
                    case "init-param": {
                        KeyValuePair pair = collectParams(node);
                        initParams.put(pair.getKey(), pair.getValue());
                        break;
                    }
                }
            }

            JerryServletRegistration jerryServletRegistration = new JerryServletRegistration(servletName, servletClass);
            jerryServletRegistration.setInitParameters(initParams);
            jerryServletRegistration.setLoadOnStartup(loadOnStartup);

            servletContext.addServletRegistration(jerryServletRegistration);
        }

        private void collectServletMapping(Node mapping){
            NodeList urls = mapping.getChildNodes();

            String servletName = null;
            Set<String> servletPatterns = new HashSet<>();

            for (int i = 0; i < urls.getLength(); i++){
                Node url = urls.item(i);
                switch (url.getNodeName()){
                    case "servlet-name":
                        servletName = url.getTextContent();
                        break;
                    case "url-pattern":
                        servletPatterns.add(url.getTextContent());
                        break;
                }
            }

            servletContext.getServletRegistration(servletName).addMapping(servletPatterns.toArray(new String[servletPatterns.size()]));
        }

        private KeyValuePair collectParams(Node parameters){
            NodeList params = parameters.getChildNodes();

            String name = null;
            String value = null;

            for (int i = 0; i < params.getLength(); i++){
                Node param = params.item(i);
                switch (param.getNodeName()){
                    case "param-name":
                        name = param.getTextContent();
                        break;
                    case "param-value":
                        value = param.getTextContent();
                        break;
                }
            }
            return new KeyValuePair(name, value);
        }

        private void collectSecurityConstraint(Node constraint){
            NodeList elements = constraint.getChildNodes();

            Set<String> roles = null;
            String transportGuarantee = null;

            for(int i = 0; i < elements.getLength(); i++){
                Node element = elements.item(i);
                switch (element.getNodeName()){
                    case "web-resource-collection": {
                        collectWebResourceCollection(element);

                        break;
                    }
                    case "auth-constraint": {
                        roles = collectAuthConstraint(element);
                        break;
                    }
                    case "user-data-constraint": {
                        transportGuarantee = collectUserDataConstraint(element);
                        break;
                    }
                }
            }
        }

        private HttpMethodConstraintElement collectWebResourceCollection(Node resourceCollection){
            NodeList elements = resourceCollection.getChildNodes();

            String name = null;
            Set<String> urls = new HashSet<>();
            Set<String> methods = new HashSet<>();

            for(int i = 0; i < elements.getLength(); i++){
                Node element = elements.item(i);
                switch (element.getNodeName()){
                    case "web-resource-name": {
                        name = element.getTextContent();
                        break;
                    }
                    case "url-pattern": {
                        urls.add(element.getTextContent());
                        break;
                    }
                    case "http-method": {
                        methods.add(element.getTextContent());
                        break;
                    }
                }
            }

            return null;
        }

        private Set<String> collectAuthConstraint(Node auth){
            NodeList elements = auth.getChildNodes();

            Set<String> roles = new HashSet<>();

            for(int i = 0; i < elements.getLength(); i++){
                Node element = elements.item(i);
                switch (element.getNodeName()){
                    case "role-name": {
                        roles.add(element.getTextContent());
                        break;
                    }
                }
            }

            return roles;
        }

        private String collectUserDataConstraint(Node data){
            NodeList elements = data.getChildNodes();

            String transportGuarantee = null;

            for(int i = 0; i < elements.getLength(); i++){
                Node element = elements.item(i);
                switch (element.getNodeName()){
                    case "transport-guarantee": {
                        transportGuarantee = element.getTextContent();
                        break;
                    }
                }
            }

            return transportGuarantee;
        }
    }
}
