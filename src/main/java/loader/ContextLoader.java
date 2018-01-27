package loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import servlet.WarContext;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ContextLoader implements Loader<ServletContext> {

    private static final String WEB_INF = "WEB-INF";
    private static final String WEB_XML = "web.xml";

    private Map<String, String> contextParams;
    private Map<String, InfoNode> servlets;
    private Map<String, InfoNode> filters;
    private Map<String, String> servletsMapping;
    private Map<String, String> filtersMapping;

    private Logger logger = LogManager.getLogger("loader.ContextLoader");

    public List<ServletContext> load(String path) {
        logger.debug("Loading contexts.");
        List<ServletContext> contexts = new LinkedList<ServletContext>();

        unzipWars(path);

        File webapps = new File(path);

        for (File file : webapps.listFiles()){
            WarContext context = createContext(file);
            contexts.add(context);
        }

        return contexts;
    }

    private WarContext createContext(File file){
        logger.debug("Creating context:" + file.getName());
        contextParams = new HashMap<>();
        servlets = new HashMap<>();
        filters = new HashMap<>();

        parseWebXml(file);
        loadJars(file);
        loadClasses(file);
        loadResources(file);

        

        return null;
    }

    private void loadResources(File file){

    }

    private void loadClasses(File file){

    }

    private void loadJars(File file){

    }

    private void parseWebXml(File file){
        logger.debug("Parsing web.xml from:" + file.getName());
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document web = documentBuilder.parse(file.getAbsolutePath() + File.separator + WEB_INF + File.separator + WEB_XML);

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
                        collectClasses(node, servlets);
                        break;
                    }
                    case "filter": {
                        collectClasses(node, filters);
                        break;
                    }
                    case "servlet-mapping":{
                        collectParams(node, servletsMapping);
                        break;
                    }
                    case "filter-mapping": {
                        collectParams(node, filtersMapping);
                        break;
                    }
                }

            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void collectClasses(Node obj, Map<String, InfoNode> map){
        NodeList params = obj.getChildNodes();
        String name = null;
        String clazz = null;
        Map<String, String> initParams = new HashMap<>();

        for (int j = 0; j < params.getLength(); j++){
            Node node = params.item(j);

            if(node.getNodeName().contains("-name")){
                name = node.getNodeValue();
                continue;
            }

            if(node.getNodeName().contains("-class")){
                clazz = node.getNodeValue();
                continue;
            }

            if(node.getNodeName().contains("init-param")){
                collectParams(node, initParams);
                continue;
            }

        }
        map.put(name, new InfoNode(clazz, initParams));
    }

    private void collectParams(Node parameters, Map<String, String> map){
        NodeList params = parameters.getChildNodes();
        Node paramName = params.item(0);
        Node paramValue = params.item(1);
        map.put(paramName.getNodeValue(), paramValue.getNodeValue());
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

    private class InfoNode {

        private String clazz;
        private Map<String, String> params;

        public InfoNode(String clazz, Map<String, String> params){
            this.clazz = clazz;
            this.params = params;
        }

        public String getClazz() {
            return clazz;
        }

        public Map<String, String> getParams() {
            return params;
        }
    }

}
