package loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servlet.WarContext;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ContextLoader implements Loader<ServletContext> {

    private Logger logger = LogManager.getLogger("loader.ContextLoader");

    public List<ServletContext> load(String path) {
        logger.info("Loading contexts.");
        List<ServletContext> contexts = new LinkedList<ServletContext>();

        logger.info("Unzipping WARs.");
        unzipWars(path);

        File webapps = new File(path);

        for (File file : webapps.listFiles()){
            WarContext context = createContext(file);
            contexts.add(context);
        }

        return contexts;
    }

    private WarContext createContext(File file){
        return null;
    }

    private void unzipWars(String path){
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

}
