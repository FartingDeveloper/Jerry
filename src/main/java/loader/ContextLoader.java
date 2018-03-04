package loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.omg.CORBA.NameValuePair;
import org.springframework.beans.factory.BeanCreationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import servlet.registration.JerryFilterRegistration;
import servlet.context.JerryServletContext;
import servlet.registration.JerryServletRegistrationDynamic;

import javax.servlet.DispatcherType;
import javax.servlet.ServletSecurityElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ContextLoader implements Loader<Map<String, JerryServletContext>> {

    private static final String WEB_INF = "WEB-INF";
    private static final String WEB_XML = "web.xml";
    private static final String LIB = "lib";
    private static final String CLASSES = "classes";

    private Map<String, JerryServletContext> contexts = new HashMap<>();
    private ClassLoader classLoader;
    private Set<String> resources;

    private Logger logger = LogManager.getLogger("loader.ContextLoader");

    private WebXmlParser parser = new WebXmlParser();

    public Map<String, JerryServletContext> load(String path) {
        logger.debug("Loading contexts.");
        clear();

        unzipWars(path);

        File webapps = new File(path);

        for (File file : webapps.listFiles()){
            if(! isWar(file)){
                JerryServletContext context = createContext(file);
                contexts.put(file.getName(), context);
            }
        }

        return contexts;
    }

    private void clear(){
        contexts.clear();
    }

    private JerryServletContext createContext(File file){
        logger.debug("Creating context:" + file.getName());

        File jars = new File(file.getAbsolutePath() + File.separator + WEB_INF + File.separator + LIB);
        File classes = new File(file.getAbsolutePath() + File.separator + WEB_INF + File.separator + CLASSES);
        classLoader = loadClasses(jars, classes);

        resources = loadResources(file);

        File webXml = new File(file.getAbsolutePath() + File.separator + WEB_INF + File.separator + WEB_XML);
        logger.debug("Parsing web.xml from:" + file.getName());

        return parser.parseWebXml(webXml);
    }

    private Set<String> loadResources(File folder){
        Set<String> res = new HashSet<>();
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
               res.addAll(loadResources(fileEntry));
            } else {
                res.add(fileEntry.getPath());
            }
        }
        return res;
    }

    private ClassLoader loadClasses(File jars, File classes){
        URLClassLoader loader = null;

        File[] files = jars.listFiles();
        int size = files.length + 1;

        try {
            URL[] urls = new URL[files.length + 1];
            urls[size - 1] = classes.toURI().toURL();

            for(int i = 0; i < size - 1; i++){
                urls[i] = files[i].toURI().toURL();
            }

            loader = new URLClassLoader(urls, this.getClass().getClassLoader());
        } catch (MalformedURLException e) {
            logger.error("Failed to load jars and classes.");
            throw new BeanCreationException(e.toString());
        }
        return loader;
    }

    private void unzipWars(String path){
        logger.debug("Unzipping WARs.");
        File webapps = new File(path);
        for (File app : webapps.listFiles()){
            if(isWar(app)){
                JarFile war = null;
                try {
                    war = new JarFile(app);
                    extractWar(war);
                } catch (IOException e) {
                    logger.error("WAR unzipping error.");
                }
                finally {
                    try {
                        war.close();
                    } catch (IOException e) {
                        e.printStackTrace();
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
                logger.error("WAR extracting error.");
            }

            if(entries.hasMoreElements() == false){
                System.out.println("FART");
            }
        }


        return outputDir;
    }

    private String makeOutputDir(String warName){
        int index = warName.indexOf(".war");
        return warName.substring(0, index);
    }

    private class WebXmlParser {

        private JerryServletContext servletContext;

        public JerryServletContext parseWebXml(File file){

            servletContext = new JerryServletContext(contexts, resources, classLoader);
            servletContext.setContextPath(file.getName());

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
                    }
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new BeanCreationException("Can't find" + file.getName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
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

            JerryFilterRegistration jerryFilterRegistration = new JerryFilterRegistration(filterName, filterClass, initParams);

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
            String role = null;
            ServletSecurityElement securityRoleRef = new ServletSecurityElement();

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
                    case "jsp-file": {

                        break;
                    }
                    case "init-param": {
                        KeyValuePair pair = collectParams(node);
                        initParams.put(pair.getKey(), pair.getValue());
                        break;
                    }
                    case "load-on-startup":{
                        loadOnStartup = Integer.valueOf(node.getTextContent());
                        break;
                    }
                    case "run-as": {
                        role = node.getTextContent();
                        break;
                    }
                    case "security-role-ref": {

                    }
                }
            }

            JerryServletRegistrationDynamic jerryServletRegistration = new JerryServletRegistrationDynamic(servletName, servletClass);
            jerryServletRegistration.setInitParameters(initParams);
            jerryServletRegistration.setLoadOnStartup(loadOnStartup);
            jerryServletRegistration.setRunAsRole(role);

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
    }
}
