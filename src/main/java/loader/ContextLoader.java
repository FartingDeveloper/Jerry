package loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanCreationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import servlet.JerryFilterRegistration;
import servlet.JerryServletContext;
import servlet.JerryServletRegistration;
import servlet.JerryServletRegistrationDynamic;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletSecurityElement;
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

        File resourcesPath = new File(file.getAbsolutePath() + File.separator + WEB_INF);
        resources = loadResources(resourcesPath);

        File webXml = new File(file.getAbsolutePath() + File.separator + WEB_INF + File.separator + WEB_XML);
        logger.debug("Parsing web.xml from:" + file.getName());

        return parser.parseWebXml(webXml);
    }

    private Set<String> loadResources(File resources){

        return null;
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
                try {
                    JarFile war = new JarFile(app);
                    extractWar(war, app.getAbsolutePath());
                } catch (IOException e) {
                    logger.error("WAR unzipping error.");
                }
            }
        }
    }

    private boolean isWar(File file){
        String fileName = file.getName();
        String extension;
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            extension = fileName.substring(fileName.lastIndexOf(".")+1);
            if(extension == "war"){
                return true;
            }
        }
        return false;
    }

    private File extractWar(JarFile war, String outputPath){
        String outputDirName = makeOutputDir(war.getName(), outputPath);
        File outputDir = new File(outputDirName);

        if(! outputDir.exists()){
            outputDir.mkdirs();
        }

        Enumeration<JarEntry> entries = war.entries();

        while (entries.hasMoreElements()){
            JarEntry entry = entries.nextElement();
            File file = new File(outputDirName + File.separator + entry.getName());
            new File(file.getParent()).mkdirs(); //sub directories

            try(InputStream in = war.getInputStream(entry); OutputStream out = new FileOutputStream(file)) {
                BufferedInputStream bufferedIn = new BufferedInputStream(in);
                BufferedOutputStream bufferedOut = new BufferedOutputStream(out);
                byte[] buffer = new byte[1024];

                while (bufferedIn.read(buffer) != -1){
                    bufferedOut.write(buffer);
                    bufferedOut.flush();
                }

            } catch (IOException e) {
                logger.error("WAR extracting error.");
            }
        }

        return outputDir;
    }

    private String makeOutputDir(String warName, String outputPath){
        return outputPath.substring(0, outputPath.lastIndexOf(warName));
    }

    private class WebXmlParser {

        private Map<String, String> contextParams = new HashMap<>();

        private Map<String, JerryServletRegistration> servlets = new HashMap<>();
        private Map<String, Set<String>> servletMapping = new LinkedHashMap<>();

        private Map<String, JerryFilterRegistration> filters = new HashMap<>();

        private Map<String, Set<String>> filterMapping = new LinkedHashMap<>();
        private Map<Set<String>, Set<DispatcherType>> dispatcherTypes = new HashMap<>();

        private Map<String, Set<String>> filterServletNameMapping = new LinkedHashMap<>();
        private Map<Set<String>, Set<DispatcherType>> servletNameDispatcherTypes = new HashMap<>();

        private Set<EventListener> listeners = new LinkedHashSet<>();

        public JerryServletContext parseWebXml(File file){
            clear();

            try {
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document web = documentBuilder.parse(file);

                Node root = web.getDocumentElement();
                NodeList childNodes = root.getChildNodes();

                for(int i = 0; i < childNodes.getLength(); i++){
                    Node node = childNodes.item(i);
                    switch (node.getNodeName()){
                        case "context-param": {
                            collectParams(node, contextParams);
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

            addMappings();
            JerryServletContext servletContext = new JerryServletContext(file.getPath(), contextParams, servlets, filters,
                    listeners, resources, null, contexts, classLoader);

            return servletContext;
        }

        private void clear(){
            contextParams.clear();
            servlets.clear();
            filters.clear();
            servletMapping.clear();
            filterMapping.clear();
            filterServletNameMapping.clear();
        }

        private void addMappings(){
            for (String servletName : servlets.keySet()){
                JerryServletRegistration servlet = servlets.get(servletName);

                Set<String> mapping = servletMapping.get(servletName);
                servlet.addMapping(mapping.toArray(new String[mapping.size()]));
                servlet.setInitialized(true);
            }

            for (String filterName : filters.keySet()){
                JerryFilterRegistration filterRegistration = filters.get(filterName);

                Set<String> mapping = filterMapping.get(filterName);
                Set<DispatcherType> dispatcherTypesMapping = dispatcherTypes.get(mapping);
                filterRegistration.addMappingForUrlPatterns(EnumSet.copyOf(dispatcherTypesMapping), false, mapping.toArray(new String[mapping.size()]));

                Set<String> servletNameMapping = filterServletNameMapping.get(filterName);
                Set<DispatcherType> dispatcherTypesServletNameMapping = servletNameDispatcherTypes.get(servletNameMapping);
                filterRegistration.addMappingForServletNames(EnumSet.copyOf(dispatcherTypesServletNameMapping), false, servletNameMapping.toArray(new String[servletNameMapping.size()]));

                filterRegistration.setInitialized(true);
            }
        }

        private void loadListeners(){

        }

        private void collectListeners(Node node) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
            NodeList listener = node.getChildNodes();

            for (int i = 0; i < listener.getLength(); i++){
                Node listenerClass = listener.item(i);

                switch (listenerClass.getNodeName()){
                    case "listener-class": {
                        Class<? extends EventListener> clazz = (Class<? extends EventListener>) classLoader.loadClass(listenerClass.getTextContent());
                        listeners.add(clazz.newInstance());
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
                        collectParams(node, initParams);
                        break;
                    }
                }
            }

            JerryFilterRegistration jerryFilterRegistration = new JerryFilterRegistration(filterName, filterClass, initParams);
            filters.put(filterName, jerryFilterRegistration);
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

            filterMapping.put(filterName, filterPatterns);
            dispatcherTypes.put(filterPatterns, dispatcherTypeSet);

            filterServletNameMapping.put(filterName, servletNames);
            servletNameDispatcherTypes.put(servletNames, dispatcherTypeSet);
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
                        collectParams(node, initParams);
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

            servlets.put(servletName, jerryServletRegistration);

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

            servletMapping.put(servletName, servletPatterns);
        }

        private void collectParams(Node parameters, Map<String, String> map){
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
            map.put(name, value);
        }
    }
}
