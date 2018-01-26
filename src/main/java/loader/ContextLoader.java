package loader;

import servlet.WarContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;

public class ContextLoader implements Loader<ServletContext> {

    public List<ServletContext> load(String path) {
        List<ServletContext> contexts = new LinkedList<ServletContext>();

        File webapps = new File(path);
        for (File app : webapps.listFiles()){
            if(isWar(app)){
                try {
                    JarFile war = new JarFile(app);
                    File warDirectory = extractWar(war);
                    ServletContext servletContext = new WarContext(warDirectory);
                    contexts.add(servletContext);
                } catch (IOException e) {
                    e.printStackTrace(); //LOG4J
                }
            }
        }

        return contexts;
    }

    private boolean isWar(File file){
        String fileName = file.getName();
        String extension;
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            extension = fileName.substring(fileName.lastIndexOf(".")+1);
            if(extension == "war"){
                return true;
            }
            else {
                return false;
            }
        }
        else return false;
    }

    private File extractWar(JarFile war){
        File warDirectory = null;

        return warDirectory;
    }

}
